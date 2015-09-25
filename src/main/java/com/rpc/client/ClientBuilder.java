package com.rpc.client;

import com.rpc.client.netty.NettyConnectionFatory;

public class ClientBuilder {

	String url ;
	
	Class<? extends ConnectionFactory> factoryClass;
	
	Class<? extends Polocy > polocy;
	public ClientBuilder(){
		
	}
	public ClientBuilder(String url){
		this.url = url;
	}
	public Client build(){
		if (url ==null) {
			throw new IllegalArgumentException("you must input url !");
		}
		Class<? extends ConnectionFactory> factory = factoryClass!=null? factoryClass: NettyConnectionFatory.class;
		Class <? extends Polocy > pol = polocy!=null ? polocy : Polocy.class;
		return new Client(new DefaultConnectionManager(new TransportURL(url) ,factory , pol));
	}
	public static ClientBuilder get(){
		return new ClientBuilder();
	}
	public static ClientBuilder get(String url){
		return new ClientBuilder(url);
	}
	public ClientBuilder factory(Class<? extends ConnectionFactory> factory){
		this.factoryClass = factory;
		return this;
	}
	public ClientBuilder polocy(Class<? extends Polocy >  polocy){
		this.polocy = polocy;
		return this;
	}
	
	
}
