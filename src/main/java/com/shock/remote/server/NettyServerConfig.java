package com.shock.remote.server;

/**
 * Created by shocklee on 16/6/28.
 */
public class NettyServerConfig {

    private int listenPort = 8888;
    private int serverWorkerThreads = 8;
    private int serverExecutorThreads = 0;
    private int serverSelectorThreads = 3;
    private int serverMaxConcurrencyRequest = 64;
    private int serverChannelMaxIdleTimeSeconds = 120;
    private int serverSocketSndBufSize = 65535;
    private int serverSocketRcvBufSize = 65535;
    private boolean serverPooledByteBufAllocatorEnable = false;


    public int getListenPort() {
        return listenPort;
    }


    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }


    public int getServerWorkerThreads() {
        return serverWorkerThreads;
    }


    public void setServerWorkerThreads(int serverWorkerThreads) {
        this.serverWorkerThreads = serverWorkerThreads;
    }


    public int getServerSelectorThreads() {
        return serverSelectorThreads;
    }


    public void setServerSelectorThreads(int serverSelectorThreads) {
        this.serverSelectorThreads = serverSelectorThreads;
    }



    public int getServerExecutorThreads() {
        return serverExecutorThreads;
    }


    public void setServerExecutorThreads(int serverExecutorThreads) {
        this.serverExecutorThreads = serverExecutorThreads;
    }


    public int getServerMaxConcurrencyRequest() {
        return serverMaxConcurrencyRequest;
    }


    public void setServerMaxConcurrencyRequest(int serverMaxConcurrencyRequest) {
        this.serverMaxConcurrencyRequest = serverMaxConcurrencyRequest;
    }


    public int getServerChannelMaxIdleTimeSeconds() {
        return serverChannelMaxIdleTimeSeconds;
    }


    public void setServerChannelMaxIdleTimeSeconds(int serverChannelMaxIdleTimeSeconds) {
        this.serverChannelMaxIdleTimeSeconds = serverChannelMaxIdleTimeSeconds;
    }

    public int getServerSocketSndBufSize() {
        return serverSocketSndBufSize;
    }


    public void setServerSocketSndBufSize(int serverSocketSndBufSize) {
        this.serverSocketSndBufSize = serverSocketSndBufSize;
    }

    public int getServerSocketRcvBufSize() {
        return serverSocketRcvBufSize;
    }


    public void setServerSocketRcvBufSize(int serverSocketRcvBufSize) {
        this.serverSocketRcvBufSize = serverSocketRcvBufSize;
    }


    public boolean isServerPooledByteBufAllocatorEnable() {
        return serverPooledByteBufAllocatorEnable;
    }


    public void setServerPooledByteBufAllocatorEnable(boolean serverPooledByteBufAllocatorEnable) {
        this.serverPooledByteBufAllocatorEnable = serverPooledByteBufAllocatorEnable;
    }
}
