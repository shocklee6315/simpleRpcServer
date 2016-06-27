package com.rpc.serializer;

import java.util.UUID;

/**
 * Created by shocklee on 16/6/27.
 */
public class RpcMessage {

    public RpcMessage(){
        this.messageId= UUID.randomUUID().toString();
    }

    public RpcMessage(String id){
        this.messageId = id;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    private short version;

    public short getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(short rtnCode) {
        this.rtnCode = rtnCode;
    }

    public short getMessageType() {
        return messageType;
    }

    public void setMessageType(short messageType) {
        this.messageType = messageType;
    }

    private short rtnCode;
    private short messageType;//消息类型,1请求,2响应 ,3 心跳请求 4 心跳相应

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    private String messageId ;

    private byte[] body;//消息体

}
