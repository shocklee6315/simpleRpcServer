package com.rpc.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransportURL {

	String orgin ;
	
	TransportProtocol protocol;
	
	List<AddressInfo> lists = new ArrayList<AddressInfo>();
	
	Map<String , String> params = new HashMap<String , String>();
	
	public TransportURL(String url){
		this.orgin = url;
		int idx1 = url.indexOf("://");
		if (idx1 <= 0) {
			throw new IllegalArgumentException("Illegal url:" + url);
		}
		int idx2 = url.indexOf('?', idx1 + 4);
		this.protocol = TransportProtocol.valueOf(url.substring(0, idx1).toLowerCase());
		
		String urls = "";
		if(idx2 >0)
			urls = url.substring(idx1 + 3, idx2);
		else
			urls = url.substring(idx1 + 3);
		String[] hosts = urls.split(",");
		
		for(String hostport :hosts){
			if(hostport.isEmpty())continue;
			int idx3 = hostport.indexOf(':');
			String host = idx3 <0? hostport:hostport.substring(0 ,idx3);
			int port = idx3 <0 ?protocol.getDefaultPort() :Integer.parseInt(hostport.substring(idx3 + 1));
			lists.add(new AddressInfo(host,port));
		}
		if(lists.size() ==0){
			throw new IllegalArgumentException("Illegal url:" + url);
		}
		if(idx2 >0){
			String query = url.substring(idx2+1);
			String[] paras = query.split("&");
			for(String p: paras){
				if(!p.isEmpty()){
					String[] pp =p.split("=");
					if(pp.length ==2){
						params.put(pp[0],pp[1]);
					}
				}
			}
			
		}
		
	}
	
	public TransportProtocol getProtocol(){
		return this.protocol;
	}
	
	public List<AddressInfo> getHosts(){
		return this.lists;
	}
	
	public Map<String , String>  getParam(){
		return this.params;
	}
	
	public String toString(){
		return String.format("protocol = %s , serverlist = %s  , params = %s ", protocol ,lists.toString() ,params);
	}
	
	public static void main(String[] args) {
		System.out.println(new TransportURL("tcp://127.0.0.1:8091,127.0.0.1?affefe=afef&fafe=aaa&bbes=222"));
	}
}
