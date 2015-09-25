package com.rpc.client;

import java.util.Random;

public class Polocy {

	int len ;
	public Polocy(int len){
		this.len = len;
	}
	
	Random r = new Random();
	public int next(){
		return r.nextInt(len);
	}
}
