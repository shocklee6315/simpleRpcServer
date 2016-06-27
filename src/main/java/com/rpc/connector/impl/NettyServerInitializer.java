package com.rpc.connector.impl;

import com.rpc.serializer.RpcRequest;
import com.rpc.serializer.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by shocklee on 16/6/26.
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new IdleStateHandler(6, 0, 0));
        p.addLast(new LengthFieldBasedFrameDecoder(65535,0,4,0,4));
        p.addLast(new LengthFieldPrepender(4));
        //rpc消息解码器
        p.addLast(new RpcMessageProtoBufDecoder());
        p.addLast(new RpcMessageProtoBufEncoder());
        p.addLast(new ServerHeartBeatHandler(300));
        p.addLast(new NettyRpcServerHandler());
    }
}
