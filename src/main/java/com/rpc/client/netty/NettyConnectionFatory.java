package com.rpc.client.netty;

import com.rpc.client.Connection;
import com.rpc.client.ConnectionFactory;

public class NettyConnectionFatory implements ConnectionFactory{

	@Override
	public Connection createConnection(String host, int port) throws Exception {
		return new NettyConnection(host ,port);
	}

}
