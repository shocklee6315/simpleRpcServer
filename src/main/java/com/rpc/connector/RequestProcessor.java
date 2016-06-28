package com.rpc.connector;

import com.rpc.serializer.RpcMessage;

/**
 * Created by shocklee on 16/6/28.
 */
public interface RequestProcessor {

    public RpcMessage processRequest(RpcMessage request) throws Exception;

}
