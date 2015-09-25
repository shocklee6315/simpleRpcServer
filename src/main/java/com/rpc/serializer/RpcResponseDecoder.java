package com.rpc.serializer;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcResponseDecoder extends ByteToMessageDecoder{

	
	ClientSerializer serializer;
	public RpcResponseDecoder(ClientSerializer serialzer){
		this.serializer = serialzer;
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		if (in.readableBytes() < 4) {
			return ;
		}
		int length =in.getInt(in.readerIndex());
		if( in.readableBytes() < length +4){
			return ;
		}
		
		ByteBufInputStream ins = new ByteBufInputStream(in);
		RpcResponse response = serializer.decodeResponse(ins);
		out.add(response);
	}

}
