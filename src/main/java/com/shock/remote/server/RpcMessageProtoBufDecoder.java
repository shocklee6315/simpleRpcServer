package com.shock.remote.server;

import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.shock.remote.protocol.RemoteMessage;
import com.shock.remote.common.IOUtils;
import com.shock.remote.common.SchemaCache;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by shocklee on 16/6/27.
 */
public class RpcMessageProtoBufDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length =in.readableBytes();
        if(length ==0){
            return;
        }
        ByteBufInputStream ins = new ByteBufInputStream(in);
        Schema schema = SchemaCache.getSchema(RemoteMessage.class);
        byte[] bytes = new byte[length];
        IOUtils.readFully(ins, bytes, 0, length);
        RemoteMessage request = new RemoteMessage();
        ProtobufIOUtil.mergeFrom(bytes, request, schema);
        out.add(request);
    }
}
