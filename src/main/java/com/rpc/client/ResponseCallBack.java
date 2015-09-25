package com.rpc.client;

import com.rpc.serializer.RpcResponse;

public class ResponseCallBack<T> implements Callback<RpcResponse> {

	Callback<T>callback;
	
	public ResponseCallBack(Callback<T> c){
		this.callback = c;
	}
	
	@Override
	public void handleError(Throwable error) {
		callback.handleError(error);
	}

	@Override
	public void handleResult(RpcResponse result) {
		Object rtn =result.getResult();
		callback.handleResult((T)rtn);
	}

}
