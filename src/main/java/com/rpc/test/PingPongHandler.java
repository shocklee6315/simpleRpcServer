package com.rpc.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by shocklee on 16/6/24.
 */
public class PingPongHandler extends ChannelHandlerAdapter {

    static String ping = "PING"+System.getProperty("line.separator");

    static String pong = "PONG"+System.getProperty("line.separator");

    int count =0;

    //最大多少次PING消息丢失就自动关闭连接
    int maxidle ;

    public PingPongHandler(int maxidle){
        this.maxidle = maxidle;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        if("PING".equalsIgnoreCase(msg.toString())){
            System.out.println("收到心跳消息");
            ByteBuf buff = ctx.alloc().buffer();
            buff.writeBytes(pong.getBytes());
            ctx.writeAndFlush(buff);
        }else if("PONG".equalsIgnoreCase(msg.toString())){
            //心跳返回计数清零
            count =0;
        }else {
            ctx.fireChannelRead(msg);
        }
    }
    public void userEventTriggered(
            ChannelHandlerContext ctx, Object evt)
            throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                //读数据超时
                count++;
                if(count >maxidle){
                    ctx.channel().close();
                }else {
                    ByteBuf buff = ctx.alloc().buffer();
                    buff.writeBytes(ping.getBytes());
                    ctx.writeAndFlush(buff);
                }
            } else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("write idle");
            } else if (event.state() == IdleState.ALL_IDLE) {
                System.out.println("all idle");
            }
        }
    }

}
