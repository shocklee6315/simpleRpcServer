package com.shock.remote.server;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.shock.remote.protocol.RemoteMessage;
import com.shock.remote.common.SchemaCache;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;

/**
 * Created by shocklee on 16/6/27.
 */
public class RpcMessageProtoBufEncoder extends MessageToByteEncoder<RemoteMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RemoteMessage msg, ByteBuf out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        LinkedBuffer buffer = LinkedBuffer.allocate(4096);
        Schema schema = null;
        schema = SchemaCache.getSchema(RemoteMessage.class);
        ProtobufIOUtil.writeTo(buffer, msg, schema);
        LinkedBuffer.writeTo(baos, buffer);
        out.writeBytes(baos.toByteArray());
    }
}
