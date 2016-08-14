package com.rpc.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shocklee on 16/6/24.
 */
public class ConnectionClientTest {
    static byte[] req = ("来自客户端的消息" + System.getProperty("line.separator")).getBytes();
    static Set<ChannelHandlerContext> aset = new HashSet<ChannelHandlerContext>();

    public static void main(String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group).channel(NioSocketChannel.class).option(
                ChannelOption.TCP_NODELAY, true).handler(
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new LoggingHandler(LogLevel.INFO));
                        p.addLast(new LineBasedFrameDecoder(1024)).addLast(
                                new StringDecoder())/*.addLast(new PingPongHandler(3))*/.addLast(new ChannelHandlerAdapter() {
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("获取消息" + msg.toString());
                            }

                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                aset.add(ctx);
                                System.out.println("channelActive");
                                ByteBuf message = null;
                                message = Unpooled.buffer(1000);
                                message.writeBytes(req);
                                ctx.writeAndFlush(message);
                            }

                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                cause.printStackTrace();
                                ;
                                ctx.fireExceptionCaught(cause);
                                ctx.close();
                            }

                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("channelInactive");
                                ctx.fireChannelInactive();
                            }
                        });

                    }
                });

        final ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8082)).sync();

        new Thread() {
            public void run() {
                int i = 0;
                while (true) {
                    //每过一会发送一条消息到客户端
                    if (aset.size() > 0) {
                        ChannelHandlerContext ctx = aset.iterator().next();
                        byte[] msg = ("这条是客户端发送过来的消息" + System.currentTimeMillis() + System.getProperty("line.separator")).getBytes();
                        ByteBuf message = Unpooled.buffer(100);
                        message.writeBytes(msg);
                        ctx.writeAndFlush(message).addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if(!future.isSuccess()){
                                    System.out.println("客户端消息发送失败"+Thread.currentThread());
                                    future.cause().printStackTrace();
                                }
                            }
                        });
//                        if(i> 10){
//                            ctx.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
//                        }
                        i++;
                        if (i > 10) {
                            break;
                        }
                    }
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("循环一次");

                }

            }
        }.start();


        future.channel().closeFuture().sync();
    }
}
