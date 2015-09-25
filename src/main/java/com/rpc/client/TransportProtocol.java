package com.rpc.client;

public enum TransportProtocol {

	tcp(80), http(80);

	private int defaltPort;

	private TransportProtocol(int defaltPort) {
		this.defaltPort = defaltPort;
	}

	public int getDefaultPort() {
		return this.defaltPort;
	}
	
}
