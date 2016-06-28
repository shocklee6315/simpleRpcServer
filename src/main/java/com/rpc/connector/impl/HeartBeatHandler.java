package com.rpc.connector.impl;

import com.rpc.serializer.RpcMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * Created by shocklee on 16/6/26.
 */
public class HeartBeatHandler extends ChannelHandlerAdapter{

    int count =0;
    int maxidle ;
    public HeartBeatHandler(int maxidle){
        this.maxidle = maxidle;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        if(msg instanceof RpcMessage) {
            RpcMessage rpcMessage = (RpcMessage)msg;
            if (rpcMessage.isHeartBreat()) {
                if(rpcMessage.isResponseType()){
                    count = 0;
                }else{
                    RpcMessage pong = new RpcMessage();
                    pong.markHeartBreat();
                    pong.markResponseType();
                    ctx.writeAndFlush(pong);
                }
            }else{
                ctx.fireChannelRead(msg);
            }
            return ;
        }
        ctx.fireChannelRead(msg);
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                //读数据超时
                count++;
                if (count >= maxidle) {
                    ctx.channel().close();
                } else {
                    RpcMessage ping = new RpcMessage();
//                    ping.markResponseType();
                    ping.markHeartBreat();
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
