package com.rpc.connector.impl;

import com.rpc.connector.Connector;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by shocklee on 16/6/26.
 */
public class NettyConnector implements Connector{

    private  static transient  Logger logger = LoggerFactory.getLogger(NettyConnector.class);

    ChannelFuture  serverChannelFuture ;
    public void setPort(int port) {
        this.port = port;
    }

    int port ;

    public int getAcceptThreads() {
        return acceptThreads>0 ? acceptThreads:0;
    }

    public void setAcceptThreads(int acceptThreads) {
        this.acceptThreads = acceptThreads;
    }

    int acceptThreads;

    public int getWorkThreads() {
        return workThreads >0?workThreads:0;
    }

    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }

    int workThreads;

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(getAcceptThreads());
        EventLoopGroup workerGroup = new NioEventLoopGroup(getWorkThreads());
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(
                    NioServerSocketChannel.class).handler(
                    new LoggingHandler(LogLevel.ERROR)).childHandler(
                    new NettyServerInitializer());
            try {
                final ChannelFuture future = b.bind(port()).sync();
                serverChannelFuture = future;
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error("发生系统异常" ,e);
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        serverChannelFuture.channel().close();
    }

    @Override
    public String getProtocol() {
        return "rpc";
    }

    @Override
    public int port() {
        return port>1?port:8081;
    }


}
