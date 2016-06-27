package com.rpc.connector.impl;

import com.rpc.serializer.RpcMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * Created by shocklee on 16/6/26.
 */
public class ServerHeartBeatHandler extends ChannelHandlerAdapter{

    int count =0;
    int maxidle ;
    public ServerHeartBeatHandler(int maxidle){
        this.maxidle = maxidle;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{

        if(msg instanceof RpcMessage) {
            if (((RpcMessage) msg).getMessageType() == 3) {
                RpcMessage pong = new RpcMessage();
                pong.setMessageType((short)4);
                ctx.writeAndFlush(pong);
            } else if (((RpcMessage) msg).getMessageType() == 4) {
                count = 0;
            } else{
                ctx.fireChannelRead(msg);
            }
            return ;
        }
        ctx.fireChannelRead(msg);
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                //读数据超时
                count++;
                if (count >= maxidle) {
                    ctx.channel().close();
                } else {
                    RpcMessage ping = new RpcMessage();
                    ping.setMessageType((short) 3);
                    ctx.writeAndFlush(ping);
                }
            }
        }
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
        //如果通道关闭了,那就移除掉
        ctx.fireChannelInactive();
    }
}
