package com.shock.remote.server;

import com.shock.remote.RemoteServer;
import com.shock.remote.RequestProcessor;
import com.shock.remote.handler.HeartBeatHandler;
import com.shock.remote.handler.RpcMessageProtoBufDecoder;
import com.shock.remote.handler.RpcMessageProtoBufEncoder;
import com.shock.remote.protocol.RemoteMessage;
import com.shock.remote.common.MessageUtil;
import com.shock.remote.common.NetUtil;
import com.shock.remote.protocol.ResponseCode;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shocklee on 16/6/26.
 */
public class NettyRemoteServer implements RemoteServer {

    private  static transient  Logger logger = LoggerFactory.getLogger(NettyRemoteServer.class);
    int port =0;
    ServerBootstrap bootstrap ;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup ;
    NettyServerConfig nettyServerConfig;
    // 处理器 通过缓冲队列大小做流控
    private final ExecutorService processorExecutor;
    // 处理器
    private RequestProcessor requestProcessor;

    public NettyRemoteServer(NettyServerConfig config){
        nettyServerConfig = config;
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);//多路选择器线程,默认设置一个
        workerGroup = new NioEventLoopGroup(nettyServerConfig.getServerWorkerThreads());//数据处理线程,解码编码使用
        int workThreadNums = nettyServerConfig.getServerExecutorThreads();//
        if (workThreadNums <= 0) {
            workThreadNums = 4;
        }
        //使用无界队列的newFixedThreadPool会出现系统过于繁忙的情况
//        this.processorExecutor = Executors.newFixedThreadPool(publicThreadNums, new ThreadFactory() {
//            private AtomicInteger threadIndex = new AtomicInteger(0);
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r, "NettyServerProcessorExecutor_" + this.threadIndex.incrementAndGet());
//            }
//        });
        int maxRequest = nettyServerConfig.getServerMaxConcurrencyRequest();
        if (maxRequest<=0){
            //给个默认大小
            maxRequest = 64;
        }
        this.processorExecutor = new ThreadPoolExecutor(workThreadNums, workThreadNums,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(maxRequest),
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyServerProcessorExecutor_" + this.threadIndex.incrementAndGet());
                    }
                });

    }


    @Override
    public void start() {

        if (requestProcessor ==null){
            throw new RuntimeException("系统需要注册请求处理器");
        }
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
                                p.addLast(new HeartBeatHandler(1));
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
            if(msg instanceof RemoteMessage){
                RemoteMessage request = (RemoteMessage)msg;
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

    public void processRpcMessage( ChannelHandlerContext ctx, RemoteMessage msg)throws Exception {

        final RemoteMessage message = msg;
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
    public void processRequest(final ChannelHandlerContext ctx,final RemoteMessage request)throws Exception{

        if (requestProcessor !=null) {
            Runnable run = new Runnable() {
                @Override
                public void run() {

                    try {
                        RemoteMessage response = requestProcessor.processRequest(request);

                        if (response != null) {
                            response.setMessageId(request.getMessageId());
                            response.markResponseType();
                            try {
                                ctx.writeAndFlush(response).addListener(new ResponseSendFutureListen());
                            } catch (Throwable e) {
                                logger.error("process request over, but response failed", e);
                                logger.error(request.toString());
                                logger.error(response.toString());
                            }
                        } else {
                            //没有处理结果 就不处理了

                        }
                    } catch (Exception e) {
                        logger.error("处理发生异常了", e);
                        RemoteMessage response = MessageUtil.createResponeMessage(
                                ResponseCode.SYSTEM_ERROR, request.getMessageId(), MessageUtil.exceptionDesc(e));
                        ctx.writeAndFlush(response).addListener(new ResponseSendFutureListen());;
                    }

                }
            };

            try {
                this.processorExecutor.submit(run);
            } catch (RejectedExecutionException e) {
                //服务器请求满了
                logger.error("服务器处理线程已经满了,无法处理请求", e);
                RemoteMessage response = MessageUtil.createResponeMessage(
                        ResponseCode.SYSTEM_BUSY, request.getMessageId(), "system  busy! ");
                ctx.writeAndFlush(response).addListener(new ResponseSendFutureListen());
            }

        }else{
            //服务器没有注册请求处理实现 理论上是不可能到这里的
            throw new RuntimeException("系统需要注册请求处理器");

        }
    }

    public void processResponse(ChannelHandlerContext ctx, RemoteMessage msg)throws Exception {

    }

    static  class ResponseSendFutureListen implements ChannelFutureListener{

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()){
                //发送失败了 向远程写数据发生了异常
                Throwable ex =future.cause();
                logger.error("write to remote endpoint exception  " , ex);
            }
        }
    }
}
