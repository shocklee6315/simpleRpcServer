package com.rpc;

import com.shock.remote.RequestProcessor;
import com.shock.remote.server.NettyRemoteServer;
import com.shock.remote.server.NettyServerConfig;
import com.shock.remote.protocol.RemoteMessage;

/**
 * Created by shocklee on 16/6/27.
 */
public class ConnectorTest {
    public static void main(String[] args) throws Exception{
        NettyServerConfig config = new NettyServerConfig();
        config.setListenPort(8081);
        config.setServerWorkerThreads(16);
        config.setServerMaxConcurrencyRequest(200);
        NettyRemoteServer server = new NettyRemoteServer(config);
        RequestProcessor processor = new RequestProcessor(){

            @Override
            public RemoteMessage processRequest(RemoteMessage request) throws Exception {
                RemoteMessage response = new RemoteMessage();
                response.setVersion(1);
                response.setRemarks("测试消息");
                //试试耗时的拒绝消息
                Thread.currentThread().sleep(1L);
                return response;
            }
        };
        server.regesterRequestProcessor(processor);
        server.start();
        System.in.read();
    }
}
