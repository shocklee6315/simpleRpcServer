package com.rpc.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.marshalling.ThreadLocalMarshallerProvider;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by shocklee on 16/6/24.
 */
public class ConnectionServerTest {
   static byte[] req = ("QUERY TIME ORDER"+System.getProperty("line.separator") ).getBytes();

    static Set<ChannelHandlerContext> aset =new HashSet<ChannelHandlerContext>();
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);
        try {
            ServerBootstrap b = new ServerBootstrap();
//            ServerBootstrap b2 = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(
                    NioServerSocketChannel.class).handler(
                    new LoggingHandler(LogLevel.INFO)).childHandler(
                    new NettyServerInitializer()).childAttr(AttributeKey.newInstance("abc"),"aaaa");
//            b2.group(bossGroup, workerGroup).channel(
//                    NioServerSocketChannel.class).handler(
//                    new LoggingHandler(LogLevel.INFO)).childHandler(
//                    new NettyServerInitializer()).bind(8082);

            try {
                final ChannelFuture future = b.bind(8081).sync();
                final ChannelFuture future2 = b.bind(8082).sync();

                new Thread(){
                    public void run(){
                        int i=0 ;
                        while (true) {
                            //每过一会发送一条消息到客户端

                            //每过一会发送一条消息到客户端
                            if(aset.size() >0) {
                                ChannelHandlerContext ctx= aset.iterator().next();
                                byte[] msg = ("这条是服务器端发送过来的消息" + System.currentTimeMillis() + System.getProperty("line.separator")).getBytes();
                                ByteBuf message = Unpooled.buffer(100);
                                message.writeBytes(msg);
                                ctx.writeAndFlush(message).addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture future) throws Exception {
                                        if(!future.isSuccess()){
                                            System.out.println("发送失败了" + Thread.currentThread());
                                            future.cause().printStackTrace();
                                        }
                                    }
                                });
//                            future.channel().pipeline().writeAndFlush(message);
                                System.out.println("循环一次" + ctx.channel().isActive() );
//                                if(i> 10){
//                                    ctx.channel().close();
//                                    ctx.channel().disconnect();
//                                }


                            }
                            i++;
                            if(i>100){
                                System.out.println("执行关闭");
                                future.channel().pipeline().close();
                            }
                            try {
                                Thread.sleep(1000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }.start();

                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } finally {

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    static class NettyServerInitializer extends ChannelInitializer<SocketChannel>{
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline p = ch.pipeline();
            p.addLast(new IdleStateHandler(6, 3, 0));
            System.out.println(ch.attr(AttributeKey.valueOf("abc")));
//            ch.pipeline().addLast(new ChannelHandlerAdapter(){
//                @Override
//                public void userEventTriggered(
//                        ChannelHandlerContext ctx, Object evt)
//                        throws Exception {
//                    if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
//                        IdleStateEvent event = (IdleStateEvent) evt;
//                        if(event.state() == IdleState.READER_IDLE)
//                            System.out.println("read idle");
//                        else if(event.state() == IdleState.WRITER_IDLE)
//                            System.out.println("write idle");
//                        else if(event.state() == IdleState.ALL_IDLE)
//                            System.out.println("all idle");
//                    }
//                }
//            });
            p.addLast(new LineBasedFrameDecoder(1024)).addLast(
                    new StringDecoder()).addLast(new PingPongHandler(3)).addLast(new ChannelHandlerAdapter(){
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
                    System.out.println(ctx);
                    System.out.println("获取消息" +msg.toString());

                }
                @Override
                public void channelActive(ChannelHandlerContext ctx)throws Exception{
                    aset.add(ctx);
                    System.out.println("channelActive");
                }

                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                    cause.printStackTrace();
                    ctx.fireExceptionCaught(cause);
                    ctx.close();
                }
                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("channelInactive");
                    //如果通道关闭了,那就移除掉
//                    aset.remove(ctx);
                    ctx.fireChannelInactive();
                }
                public void userEventTriggered(
                        ChannelHandlerContext ctx, Object evt)
                        throws Exception {
                    if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
                        IdleStateEvent event = (IdleStateEvent) evt;
                        if(event.state() == IdleState.READER_IDLE) {
                            System.out.println("read idle");
                        }
                        else if(event.state() == IdleState.WRITER_IDLE) {
                            System.out.println("write idle");
                        }
                        else if(event.state() == IdleState.ALL_IDLE) {
                            System.out.println("all idle");
                        }
                    }
                }
            });
        }
    }

}
