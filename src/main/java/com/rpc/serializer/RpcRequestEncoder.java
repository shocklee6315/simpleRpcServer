package com.rpc.serializer;

import java.io.ByteArrayOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcRequestEncoder extends MessageToByteEncoder<RpcRequest>{

	
	ClientSerializer serializer;
	public RpcRequestEncoder(ClientSerializer serializer){
		
		this.serializer = serializer;
	}
	@Override
	protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out)
			throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		serializer.encodeRequest(baos, msg);
		out.writeBytes(baos.toByteArray());
	}

}
