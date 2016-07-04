package com.shock.remote.common;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;

/**
 * Created by shocklee on 16/6/30.
 */
public class SerializeUtil {

    public static <T> byte[] protostuffEncode(T msg){
        if(msg ==null) return null;

        Schema schema = SchemaCache.getSchema(msg.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(1024);
        byte[] data = ProtobufIOUtil.toByteArray(msg, schema, buffer);
        return data;
    }
}
