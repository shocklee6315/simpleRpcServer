package com.rpc.regester;

import java.util.concurrent.ConcurrentHashMap;

public class RegestryImpl implements Regestry{

	ConcurrentHashMap<Class ,Object> map  = new ConcurrentHashMap<Class ,Object>();
	
	public RegestryImpl(){
		
	}
	public void regeser(Class inter , Object o){
		map.put(inter, o);
	}
	public Object regested(Class inter ){
		return map.get(inter);
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		map.clear();
	}
	
}
