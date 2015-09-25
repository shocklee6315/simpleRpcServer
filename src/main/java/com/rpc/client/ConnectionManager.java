package com.rpc.client;



public interface ConnectionManager {

	public Connection getConnection();
	
	public void close();
	
}
