package com.rpc;

import com.rpc.connector.impl.NettyRemoteServer;
import com.rpc.connector.impl.NettyServerConfig;

/**
 * Created by shocklee on 16/6/27.
 */
public class ConnectorTest {
    public static void main(String[] args) throws Exception{
        NettyServerConfig config = new NettyServerConfig();
        config.setListenPort(8081);
        NettyRemoteServer connector = new NettyRemoteServer(config);
        connector.start();
        System.in.read();
    }
}
