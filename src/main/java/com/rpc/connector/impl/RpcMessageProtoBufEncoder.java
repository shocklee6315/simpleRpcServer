package com.rpc.connector.impl;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.rpc.serializer.RpcMessage;
import com.rpc.util.SchemaCache;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;

/**
 * Created by shocklee on 16/6/27.
 */
public class RpcMessageProtoBufEncoder extends MessageToByteEncoder<RpcMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        LinkedBuffer buffer = LinkedBuffer.allocate(4096);
        Schema schema = null;
        schema = SchemaCache.getSchema(RpcMessage.class);
        ProtobufIOUtil.writeTo(buffer, msg, schema);
        LinkedBuffer.writeTo(baos, buffer);
        out.writeBytes(baos.toByteArray());
    }
}
