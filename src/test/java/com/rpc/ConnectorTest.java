package com.rpc;

import com.rpc.connector.impl.NettyConnector;

/**
 * Created by shocklee on 16/6/27.
 */
public class ConnectorTest {
    public static void main(String[] args) {
        NettyConnector connector = new NettyConnector();
        connector.setPort(8081);
        connector.setAcceptThreads(1);
        connector.setWorkThreads(5);
        connector.start();
    }
}
