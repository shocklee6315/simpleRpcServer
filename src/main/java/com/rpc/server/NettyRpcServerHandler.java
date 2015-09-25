package com.rpc.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.rpc.regester.RegestrySupport;
import com.rpc.serializer.RpcRequest;
import com.rpc.serializer.RpcResponse;

public class NettyRpcServerHandler extends ChannelHandlerAdapter{
	
	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if(msg instanceof RpcRequest){
			RpcRequest request = (RpcRequest)msg;
			
			RpcResponse response =new  RpcResponse(request.getRequestID());
			try {
				Object obj = handler(request);
				response.setResult(obj);
			} catch (Exception e) {
				e.printStackTrace();
				response.setException(e);
			}
			ctx.writeAndFlush(response);
		}
		
	}

	Object handler(RpcRequest request)throws Exception{
		
		String className =request.getClassName();
		String methodName =request.getMethodName();
		String[] paramTypes =request.getParameterTypes();
		List<Class> parameterTypes =  new ArrayList<Class>();
		Object obj =null;
		try {
			for(String s :paramTypes){
					parameterTypes.add(Class.forName(s));
			}
			Class clazz =Class.forName(className);
			Method m = clazz.getMethod(methodName, parameterTypes.toArray(new Class[0]));
			obj = m.invoke(RegestrySupport.getRegestry().regested(clazz), request.getParameters());
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw new RuntimeException( e.getTargetException());
		} catch (NoSuchMethodException e) {
			throw e;
		} catch (SecurityException e) {
			throw e;
		}
		
		return obj;
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}
}
