package com.rpc.serializer;

import com.rpc.common.MessageType;

import java.util.UUID;

/**
 * Created by shocklee on 16/6/27.
 */
public class RpcMessage {

    private static final int RPC_TYPE = 0; // 0, REQUEST ; 1, RESPONSE

    private static final int RPC_HEARTBREAT = 1; // 0, NO ;1, YES

    /**
     * header部分
     */
    private int version;
    private String messageId;
    private int rtnCode;
    /**
     * 消息类型标记,从低位到高位 第一位代表类型 0请求 ,1响应
     * 第二位代表心跳消息 1是 0否
     * 第三位代表..
     */
    private int msgFlag=0;


    private String remarks;
    /**
     * body部分
     */
    private byte[] body;


    public RpcMessage() {
        this.messageId = UUID.randomUUID().toString();
    }

    public RpcMessage(String id) {
        this.messageId = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(int rtnCode) {
        this.rtnCode = rtnCode;
    }

    public int getMsgFlag() {
        return msgFlag;
    }

    public void setMsgFlag(int msgFlag) {
        this.msgFlag = msgFlag;
    }


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


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void markResponseType() {
        int bits = 1 << RPC_TYPE;
        this.msgFlag |= bits;
    }

    public boolean isResponseType() {
        int bits = 1 << RPC_TYPE;
        return (this.msgFlag & bits) == bits;
    }

    public void markHeartBreat(){
        int bits = 1 << RPC_HEARTBREAT;
        this.msgFlag |= bits;
    }

    public boolean isHeartBreat(){
        int bits = 1 << RPC_HEARTBREAT;
        return (this.msgFlag & bits) == bits;
    }

    public MessageType getType() {
        if (isResponseType()) {
            return MessageType.RESPONSE_MESSAGE;
        }
        return MessageType.REQUEST_MESSAGE;
    }

}
