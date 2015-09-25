package com.rpc.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

import com.rpc.serializer.ProtobufSerializer;
import com.rpc.serializer.RpcRequest;
import com.rpc.serializer.RpcRequestEncoder;
import com.rpc.serializer.RpcResponse;
import com.rpc.serializer.RpcResponseDecoder;

@Deprecated
public class ClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		
		// Client����������
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		// ����һ������������Ϣ�͸�����Ϣ�¼�����(Handler)
		bootstrap.group(group)
         .channel(NioSocketChannel.class)
         .option(ChannelOption.TCP_NODELAY, true)
         .handler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ChannelPipeline p = ch.pipeline();
                 //p.addLast(new LoggingHandler(LogLevel.INFO));
                 p.addLast(new RpcRequestEncoder(ProtobufSerializer.getInstance()));
                 p.addLast(new RpcResponseDecoder(ProtobufSerializer.getInstance()));
                 p.addLast(new HelloClientHandler());
             }
         });
		// ���ӵ����ص�8000�˿ڵķ����
		ChannelFuture f =bootstrap.connect(new InetSocketAddress("127.0.0.1", 8091)).sync();
		// Wait until the connection is closed.
        f.channel().closeFuture().sync();
	}

	private static class HelloClientHandler extends ChannelHandlerAdapter {

		@Override
	    public void channelActive(ChannelHandlerContext ctx) {
			RpcRequest req = new RpcRequest();
			req.setRequestID("xxxxxxx");
			req.setClassName(IInterSV.class.getName());
			req.setMethodName("hello");
			req.setParameterTypes(new String[]{String.class.getName()});
			req.setParameters(new Object[]{"shocklee"});
	        ctx.writeAndFlush(req);
	    }
		@Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg){
			if(msg instanceof RpcResponse){
				RpcResponse resp = (RpcResponse)msg;
				System.out.println(resp.getResult());
			}
		}
	    
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
			System.out.println("Զ�����ӹر�");
			ctx.close();
	        
	    }
	}
}
