package com.rpc.client;

/**
 * @author lizd
 *	异步调用返回处理
 * @param <T>
 */
public interface Callback<T> {


	
  void handleResult(T result);
  


  
  void handleError(Throwable error);
}