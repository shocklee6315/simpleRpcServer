package com.rpc.connector.impl;

import com.rpc.serializer.RpcMessage;
import com.rpc.serializer.RpcRequest;
import com.rpc.serializer.RpcResponse;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by shocklee on 16/6/26.
 */
public class NettyRpcServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
         if(msg instanceof RpcMessage){
            RpcMessage request = (RpcMessage)msg;

            RpcMessage response =new  RpcMessage(request.getMessageId());
            try {
//                Object obj = handler(request);
                response.setMessageType((short) 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ctx.writeAndFlush(response);
        }

    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
        //如果通道关闭了,那就移除掉
        ctx.fireChannelInactive();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)throws Exception{
        System.out.println("channelActive");
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }
}
