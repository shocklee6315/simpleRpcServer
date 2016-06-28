package com.rpc;

import com.rpc.connector.impl.*;
import com.rpc.serializer.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * Created by shocklee on 16/6/27.
 */
public class ClientTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {

                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new LoggingHandler(LogLevel.INFO));
                        p.addLast(new LengthFieldBasedFrameDecoder(65535,0,4,0,4));
                        p.addLast(new LengthFieldPrepender(4));
                        p.addLast(new RpcMessageProtoBufDecoder());
                        p.addLast(new RpcMessageProtoBufEncoder());
                        p.addLast(new HeartBeatHandler(300));
                        p.addLast(new HelloClientHandler());
                    }
                });
        ChannelFuture f = null;
        try {
            f = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8081)).awaitUninterruptibly();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Wait until the connection is closed.
        try {
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class HelloClientHandler extends ChannelHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            RpcMessage message = new RpcMessage();
            ;
            ctx.writeAndFlush(message);
        }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg){
            if(msg instanceof RpcResponse){
                RpcResponse resp = (RpcResponse)msg;
                System.out.println(resp.getResult());
            }
            if(msg instanceof RpcMessage){
                System.out.println(((RpcMessage) msg).getMessageId());
                System.out.println(msg);
            }
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            System.out.println("发生异常了");
            ctx.close();

        }
        @Override
        public void channelInactive(ChannelHandlerContext ctx)throws Exception{
            System.out.println("发生异常了");
            ctx.close();
        }
    }
}
