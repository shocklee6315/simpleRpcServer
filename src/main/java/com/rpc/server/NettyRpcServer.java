package com.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class NettyRpcServer implements RpcServer{

	int port ;
	
	ChannelFuture  serverChannelFuture ;
	
	AtomicBoolean stopped = new AtomicBoolean(false);
	public NettyRpcServer(int port){
		this.port = port;
	}
	
	@Override
	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();

			b.group(bossGroup, workerGroup).channel(
					NioServerSocketChannel.class).handler(
					new LoggingHandler(LogLevel.INFO)).childHandler(
					new NettyRpcServerInitializer());
			try {
				ChannelFuture future = b.bind(port).sync();
				serverChannelFuture = future;
				// µÈ´ýchannel¹Ø±Õ
				future.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} finally {
			serverChannelFuture = null;
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	@Override
	public void stop() {
		serverChannelFuture.channel().close();
	}

}
