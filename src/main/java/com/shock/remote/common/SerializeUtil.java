package com.shock.remote.common;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.shock.remote.exception.SerializerException;
import com.shock.remote.protocol.RemoteMessage;
import io.netty.buffer.ByteBufInputStream;

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

    public static <T> T protostuffDecode(byte[] bytes ,Class<T> tClass)throws SerializerException{
        try {
            Schema schema = SchemaCache.getSchema(tClass);
            T obj = tClass.newInstance();
            ProtobufIOUtil.mergeFrom(bytes, obj, schema);
            return obj;
        }catch (IllegalAccessException ex){
            throw  new SerializerException(ex);
        }catch (InstantiationException ex){
            throw  new SerializerException(ex);
        }
    }

}
