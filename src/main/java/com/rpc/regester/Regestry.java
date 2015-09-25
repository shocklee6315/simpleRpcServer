package com.rpc.regester;

public interface Regestry {
	
	public void regeser(Class inter , Object o);
	
	public Object regested(Class inter );

	
	public void close();
	
}
