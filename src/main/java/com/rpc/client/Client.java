package com.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.rpc.serializer.RpcRequest;
import com.rpc.serializer.RpcResponse;
import com.shock.remote.common.ClassUtil;

public class Client {
	ConnectionManager connectionManager;
	
	
	public Client(String url){
		this.connectionManager = new DefaultConnectionManager(url);
	}
	public Client(ConnectionManager manager){
		this.connectionManager = manager;
	}
	
	public <T> T getProxy(Class<T> clazz) throws Exception{
		if(!clazz.isInterface()){
			throw new IllegalArgumentException(String.format("class %s is not an interface! " ,clazz.getName()));
		}
		return (T) Proxy.newProxyInstance(ClassUtil.getDefaultClassLoader(), new Class[]{clazz}, new ClientInvoker(clazz));
	}
	
	public <T> void callAsync(Method method ,Callback<T> callback ,Object ... args ){
		String className ;
		className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		
		List<String> parameterTypes = new LinkedList<String>();
		
		for (Class<?> parameterType : method.getParameterTypes()) {
			parameterTypes.add(parameterType.getName());
		}
		RpcRequest req = new RpcRequest(className ,methodName ,parameterTypes.toArray(new String[0]),args );
		ResponseCallBack<T> respCallback = new ResponseCallBack<T> (callback);
		connectionManager.getConnection().sendRequest(req ,respCallback);
	}
	
	public void destory(){
		connectionManager.close();
	}
	
	
	class ClientInvoker<T> implements InvocationHandler{
		
		Class<T> clazz;
		
		ClientInvoker(Class<T> cla){
			this.clazz = cla;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			
			String className = clazz.getName();
			String methodName = method.getName();
			Class<?>[] params = method.getParameterTypes();
			//����� equals ,toString hashCode 
			if(methodName.equals("equals")&& params.length == 1 && params[0].equals(Object.class)){
				Object value = args[0];
				if (value == null || !Proxy.isProxyClass(value.getClass()))
					return Boolean.FALSE;
				Object proxyHandler = Proxy.getInvocationHandler(value);
				ClientInvoker<T> handler = (ClientInvoker<T>) proxyHandler;

				return equals(handler);
			}else if(methodName.equals("hashCode")&& params.length == 0 ){
				return hashCode();
			}else if(methodName.equals("toString") && params.length == 0){
				return toString();
			}
			
			List<String> parameterTypes = new LinkedList<String>();
			for (Class<?> parameterType : params) {
				parameterTypes.add(parameterType.getName());
			}
			RpcRequest req = new RpcRequest(className ,methodName ,parameterTypes.toArray(new String[0]),args );
			
			Future<RpcResponse>  f = connectionManager.getConnection().sendRequest(req);
			try{
				return  f.get().getResult();
			}catch(ExecutionException e){
				throw e.getCause()!=null ? e.getCause(): e;
			}catch( InterruptedException ex){
				throw ex;
			}
		}
		@Override
		public int hashCode() {
			int prime = 31;
			return  Client.this.hashCode() *prime +  clazz.hashCode() ;
		}
		public boolean equals(Object value){
			if (value ==null){
				return false;
			}
			if(this ==value){
				return true;
			}
			if(value instanceof ClientInvoker){
				ClientInvoker obj = (ClientInvoker)value;
				if(obj.getClient().equals(this.getClient()) && clazz .equals(obj.clazz))
					return true;
			}
			
			return false;
		}
		
		Client getClient(){
			return Client.this;
		}

		public String toString() {
			return String.format("{client = %s , class = %s}", getClient().toString(), this.clazz.getName());
		}
	}
	
	public String toString(){
		return String.format("{URL= %s }", this.connectionManager.toString());
	}
}
