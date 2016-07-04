package com.rpc;

import com.shock.remote.protocol.RemoteMessage;
import com.shock.remote.handler.HeartBeatHandler;
import com.shock.remote.handler.RpcMessageProtoBufDecoder;
import com.shock.remote.handler.RpcMessageProtoBufEncoder;
import com.shock.remote.protocol.ResponseCode;
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
//                        p.addLast(new LoggingHandler(LogLevel.ERROR));
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

        int i =1;
        long first =0;
        long errorcount=0;
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            RemoteMessage message = new RemoteMessage();
            long start =System.currentTimeMillis();
            for (int i=0;i<800;i++) {
                ctx.writeAndFlush(message);
            }
            long end = System.currentTimeMillis();
            System.out.println("========发送耗时" + (end -start));
        }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg){
            if(i==1){
                first = System.currentTimeMillis();
            }
            if(i%800==0){
                System.out.println("10000万条读数耗时"+(System.currentTimeMillis() - first));
                System.out.println("获取错误总数"+errorcount);
                first = System.currentTimeMillis();
            }
            if(msg instanceof RemoteMessage){
//                System.out.println(((RemoteMessage) msg).getMessageId());
//                System.out.println(((RemoteMessage) msg).getRemarks());
                if(((RemoteMessage) msg).getRtnCode()!= ResponseCode.SUCCESS){
                    errorcount ++;
                }
//                System.out.println(msg);
                i++;
//                System.out.println("消息index=" + (++i));
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
