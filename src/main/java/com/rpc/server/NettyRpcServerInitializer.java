package com.rpc.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import com.rpc.serializer.ProtobufSerializer;
import com.rpc.serializer.RpcRequestDecoder;
import com.rpc.serializer.RpcResponseEncoder;

public class NettyRpcServerInitializer extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
		ChannelPipeline pipeline = ch.pipeline();
		//±àÂë½âÂëÆ÷
		pipeline.addLast(new RpcRequestDecoder(ProtobufSerializer.getInstance()));
		pipeline.addLast(new RpcResponseEncoder(ProtobufSerializer.getInstance()));
		//´¦ÀíÆ÷
		pipeline.addLast(new NettyRpcServerHandler());
	}

}
