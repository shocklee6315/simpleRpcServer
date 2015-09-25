package com.rpc.client;

public class AddressInfo {

	
	String host;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	int port;
	public AddressInfo(String host ,int port){
		this.host = host;
		this.port = port;
	}
	
	public int hashCode(){
		final int prime = 31;
		return host.hashCode() *prime + port;
	}
	
	public String toString(){
		return host +":"+port;
	}
}
