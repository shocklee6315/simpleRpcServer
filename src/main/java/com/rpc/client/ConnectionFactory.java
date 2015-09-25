package com.rpc.client;

public interface ConnectionFactory {

	public Connection createConnection(String host,int port)throws Exception;
	
}
