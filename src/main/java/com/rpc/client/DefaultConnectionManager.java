package com.rpc.client;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.rpc.client.netty.NettyConnectionFatory;

public class DefaultConnectionManager implements ConnectionManager{

	
	private ConnectionFactory _factory ;
	
	private TransportURL url ;
	private Polocy polocy;
	
	ConcurrentHashMap<AddressInfo ,Connection > cached = new ConcurrentHashMap<AddressInfo ,Connection >();
	
	
	public DefaultConnectionManager(String url){
		this(new TransportURL(url));
	}
	
	public DefaultConnectionManager(TransportURL url){
		
		this(url,NettyConnectionFatory.class);
	}
	public DefaultConnectionManager(TransportURL url ,Class<? extends ConnectionFactory> factoryClass){
		this(url ,NettyConnectionFatory.class , Polocy.class);
	}
	public DefaultConnectionManager(TransportURL url ,Class<? extends ConnectionFactory> factoryClass ,Class<? extends Polocy> polocyClass){
		this.url = url;
		this.polocy = new Polocy(url.getHosts().size());
		try {
			this._factory = factoryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public Connection getConnection(){
		AddressInfo  address =url.getHosts().get(polocy.next());
		Connection conn;
		if(cached.containsKey(address)){
			conn= cached.get(address);
		}else{
			conn= _createConnection(address);
		}
		try {
			conn.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	private synchronized Connection _createConnection(AddressInfo  address){
		if(!cached.containsKey(address)){
			try {
				Connection con = _factory.createConnection(address.getHost(), address.getPort());
				cached.put(address, con);
				
			} catch (Exception e) {
				//ingnore
			}
		}
		return cached.get(address);
	}
	
	public synchronized void close(){
		
		Iterator<Connection> itr = cached.values().iterator();
		for(;itr.hasNext();){
			Connection con =itr.next();
			con.close();
		}
	}
	public String toString(){
		return String.format("{ConnectionFactory = %s , url= %s }", this._factory.getClass().getName() , this.url.toString());
	}
}
