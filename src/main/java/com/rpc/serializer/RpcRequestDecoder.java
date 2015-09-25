package com.rpc.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcRequestDecoder extends ByteToMessageDecoder {

	
	ServerSerializer serializer ; 
	public RpcRequestDecoder(ServerSerializer serialzer){
		this.serializer = serialzer;
	}
	
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return ;
		}
		int length =in.getInt(in.readerIndex());
		if( in.readableBytes() < length +4){
			return ;
		}
		
		ByteBufInputStream ins = new ByteBufInputStream(in);
		RpcRequest request = serializer.decodeRequest(ins);
		out.add(request);
	}

}
