package com.rpc.connector.impl;

import com.rpc.common.Constants;
import com.rpc.common.MessageType;
import com.rpc.connector.RemoteServer;
import com.rpc.connector.RequestProcessor;
import com.rpc.serializer.RpcMessage;
import com.rpc.util.MessageUtil;
import com.rpc.util.NetUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shocklee on 16/6/26.
 */
public class NettyRemoteServer implements RemoteServer {

    private  static transient  Logger logger = LoggerFactory.getLogger(NettyRemoteServer.class);

    ServerBootstrap bootstrap ;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup ;
    NettyServerConfig nettyServerConfig;
    // 处理器
    private final ExecutorService processorExecutor;
    // 处理器
    private RequestProcessor requestProcessor;


    // 信号量，异步调用情况会使用，防止本地Netty缓存请求过多
    protected final Semaphore semaphoreAsync;

    public NettyRemoteServer(NettyServerConfig config){
        nettyServerConfig = config;
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(nettyServerConfig.getServerSelectorThreads());
        workerGroup = new NioEventLoopGroup(nettyServerConfig.getServerWorkerThreads());
        int publicThreadNums = nettyServerConfig.getServerCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }
        this.processorExecutor = Executors.newFixedThreadPool(publicThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyServerProcessorExecutor_" + this.threadIndex.incrementAndGet());
            }
        });
        semaphoreAsync = new Semaphore(nettyServerConfig.getServerAsyncSemaphoreValue());
    }
    int port =0;

    @Override
    public void start() {
        bootstrap.group(bossGroup, workerGroup).channel(
                NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024) //全连接队列长度
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
                .option(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
                .localAddress(new InetSocketAddress(nettyServerConfig.getListenPort()))
                .handler(new LoggingHandler(LogLevel.ERROR)) //取消日志
                .childHandler(
                        new ChannelInitializer<SocketChannel>(){
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                p.addLast(new IdleStateHandler(0, 0, nettyServerConfig.getServerChannelMaxIdleTimeSeconds()));
                                p.addLast(new LengthFieldBasedFrameDecoder(65535,0,4,0,4));
                                p.addLast(new LengthFieldPrepender(4));
                                //rpc消息解码器
                                p.addLast(new RpcMessageProtoBufDecoder());
                                p.addLast(new RpcMessageProtoBufEncoder());
                                p.addLast(new HeartBeatHandler(3));
                                p.addLast(new NettyRpcServerHandler());
                            }
                        });
        try {
            final ChannelFuture future = bootstrap.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) future.channel().localAddress();
            this.port = addr.getPort();
        } catch (InterruptedException e) {
            logger.error("发生系统异常", e);
            throw new RuntimeException("serverBootstrap.bind().sync() InterruptedException", e);
        }
    }

    @Override
    public void stop() {

        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }catch (Exception ex){
            logger.error("server stop error " ,ex);
        }
        if (processorExecutor!=null){
            try {
                processorExecutor.shutdown();
            }catch (Exception ex){
                //throw  new RuntimeException("server stop exception " ,ex);
                logger.error("server stop error " ,ex);
            }
        }

    }

    @Override
    public int listenPort() {
        return port;
    }

    @Override
    public void regesterRequestProcessor(RequestProcessor processor) {
        this.requestProcessor = processor;
    }


    class NettyRpcServerHandler extends ChannelHandlerAdapter{

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx)throws Exception {
            ctx.flush();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
            if(msg instanceof RpcMessage){
                RpcMessage request = (RpcMessage)msg;
//                RpcMessage response =new  RpcMessage(request.getMessageId());
//                try {
////                    RpcMessage response = requestProcessor.processRequest(request);
//                    response.markResponseType();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                ctx.writeAndFlush(response);
                processRpcMessage(ctx,request);
            }
        }

        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = NetUtil.parseChannelRemoteAddr(ctx.channel());
            logger.info("channelInactive, the channel[{}]", remoteAddress);
            logger.error("channelInactive==>" +ctx);
            //如果通道关闭了,那就移除掉
            ctx.fireChannelInactive();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx)throws Exception{
            final String remoteAddress = NetUtil.parseChannelRemoteAddr(ctx.channel());
            logger.info("channelActive, the channel[{}]", remoteAddress);

            ctx.fireChannelActive();
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.error("发生异常",cause);
            NetUtil.closeChannel(ctx.channel());
        }
    }

    public void processRpcMessage( ChannelHandlerContext ctx, RpcMessage msg)throws Exception {

        final RpcMessage message = msg;
        if(message !=null){
            switch (msg.getType()){
                case REQUEST_MESSAGE:
                    processRequest(ctx, message);
                    break;
                case RESPONSE_MESSAGE:
                    processResponse(ctx, message);
                    break;
                default:break;
            }

        }
    }
    public void processRequest(ChannelHandlerContext ctx, RpcMessage request)throws Exception{
        RpcMessage response =MessageUtil.createResponeMessage(Constants.ResponseCode.SUCCESS ,request.getMessageId(),"");
        ctx.writeAndFlush(response);
    }
    public void processResponse(ChannelHandlerContext ctx, RpcMessage msg)throws Exception {

    }
}
