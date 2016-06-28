package com.rpc.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rpc.client.CallFuture;
import com.rpc.client.Callback;
import com.rpc.client.Connection;
import com.rpc.serializer.ProtobufSerializer;
import com.rpc.serializer.RpcRequest;
import com.rpc.serializer.RpcRequestEncoder;
import com.rpc.serializer.RpcResponse;
import com.rpc.serializer.RpcResponseDecoder;

public class NettyConnection implements Connection{


	String host;
	int port;
	private AtomicBoolean connected  = new AtomicBoolean(false);
	ChannelFuture ch;
	ConcurrentHashMap<String, Callback<RpcResponse>> results = new ConcurrentHashMap<String, Callback<RpcResponse>>();

	Bootstrap bootstrap = new Bootstrap();
	
	
	public NettyConnection(String host, int port) {
		this.host = host;
		this.port = port;
		EventLoopGroup group = new NioEventLoopGroup();
		// ����һ������������Ϣ�͸�����Ϣ�¼�����(Handler)
		bootstrap.group(group).channel(NioSocketChannel.class).option(
				ChannelOption.TCP_NODELAY, true).handler(
				new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
//						 p.addLast(new LoggingHandler(LogLevel.INFO));
						p.addLast(new RpcRequestEncoder(ProtobufSerializer
								.getInstance()));
						p.addLast(new RpcResponseDecoder(ProtobufSerializer
								.getInstance()));
						p.addLast(new ClientHandler());
					}
				});
	}


	public synchronized void disconnect(){
		connected.set(false);
	}
	
	
	public Future<RpcResponse> sendRequest(RpcRequest request){
		
		if (!connected.get()) {
			throw new RuntimeException("δ����");
		}
		CallFuture<RpcResponse> callFuture = new CallFuture<RpcResponse> ();
		String requestId = request.getRequestID();
		results.put(requestId, callFuture);
		ch.channel().pipeline().writeAndFlush(request);
		return callFuture;
	}
	
	public synchronized void connect()throws Exception  {
		if (connected.get()) {
			return;
		}
		try {
			ch = bootstrap.connect(new InetSocketAddress(this.host, this.port)).sync();
			connected.set(true);
		} catch (Exception e) {
			throw e;
		}
	}

	public boolean connected(){
		return connected.get();
	}
	
	public void close() {
		connected.set(false);
		ch.channel().close();
		ch = null;
	}

	class ClientHandler extends ChannelHandlerAdapter {

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			System.out.println("����Զ�̷������ɹ�");

		}
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
//			System.out.println("�����Ϣ��ǰʱ��"+ System.currentTimeMillis());
			if (msg instanceof RpcResponse) {
				RpcResponse resp = (RpcResponse) msg;
				String requestId = resp.getRequestID();
				Callback<RpcResponse> callback =results.get(requestId);
				try{
					Throwable t = resp.getException();
					if(t !=null){
						callback.handleError(t);
					}else{
						callback.handleResult(resp);
					}
				}finally{
					results.remove(requestId);
				}
			}
		}

		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			NettyConnection.this.close();
			ctx.close();
			ConcurrentHashMap<String, Callback<RpcResponse>> temp = new  ConcurrentHashMap<String, Callback<RpcResponse>>();
			temp.putAll(results);
			results.clear();
			for(Iterator<Callback<RpcResponse>> itr =temp.values().iterator() ; itr.hasNext();){
				Callback<RpcResponse> call = itr.next();
				call.handleError(cause);
			}
		}
	}

	@Override
	public void sendRequest(RpcRequest request, Callback<RpcResponse> callback) {
		if (!connected.get()) {
			throw new RuntimeException("δ����");
		}
		String requestId = request.getRequestID();
		results.put(requestId, callback);
		ch.channel().pipeline().writeAndFlush(request).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {

			}
		});
		
	}
}
