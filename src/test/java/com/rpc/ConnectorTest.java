package com.rpc;

import com.rpc.remote.RequestProcessor;
import com.rpc.remote.impl.NettyRemoteServer;
import com.rpc.remote.impl.NettyServerConfig;
import com.rpc.serializer.RpcMessage;

/**
 * Created by shocklee on 16/6/27.
 */
public class ConnectorTest {
    public static void main(String[] args) throws Exception{
        NettyServerConfig config = new NettyServerConfig();
        config.setListenPort(8081);
        NettyRemoteServer server = new NettyRemoteServer(config);
        RequestProcessor processor = new RequestProcessor(){

            @Override
            public RpcMessage processRequest(RpcMessage request) throws Exception {
                RpcMessage response = new RpcMessage();
                response.setVersion(1);
                response.setRemarks("测试消息");
                //试试耗时的拒绝消息
                Thread.currentThread().sleep(1000L);
                return response;
            }
        };
        server.regesterRequestProcessor(processor);
        server.start();
        System.in.read();
    }
}
