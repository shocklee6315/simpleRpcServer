package com.rpc.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;

public class RpcResponseEncoder extends MessageToByteEncoder<RpcResponse>{

	ServerSerializer serializer;
	public RpcResponseEncoder(ServerSerializer serializer){
		
		this.serializer = serializer;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, RpcResponse msg,
			ByteBuf out) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		serializer.encodeResponse(baos, msg);
		out.writeBytes(baos.toByteArray());
	}

}
