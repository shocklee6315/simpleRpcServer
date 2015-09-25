package com.rpc.client;

import java.util.concurrent.Future;

import com.rpc.serializer.RpcRequest;
import com.rpc.serializer.RpcResponse;

public interface Connection {

	public void connect()throws Exception ;
	
	public boolean connected();
	
	public void disconnect();
	
	public void close();
	
	public Future<RpcResponse> sendRequest(RpcRequest request); 
	
	public void sendRequest(RpcRequest request , Callback<RpcResponse> callback);
}
