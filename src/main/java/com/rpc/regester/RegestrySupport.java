package com.rpc.regester;

public class RegestrySupport {

	static Regestry reg = new RegestryImpl();
	
	public static Regestry getRegestry(){
		return reg;
	}
	
}
