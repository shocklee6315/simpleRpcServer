package com.rpc.test;

import com.rpc.client.Callback;
import com.rpc.client.Client;

public class CommonClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Client client = new Client("tcp://localhost:8091");
		IInterSV intsv=client.getProxy(IInterSV.class);
//		System.out.println(intsv.hello("aabcdd"));
		while(true){
			intsv.sayHello("aabcdd");
			intsv.sayHello("aabcdd");
			Thread.sleep(100L);
		}
//		client.callAsync(IInterSV.class, IInterSV.class.getMethod("hello", String.class), new Callback<String>(){
//
//			@Override
//			public void handleError(Throwable error) {
//				// TODO Auto-generated method stub
//				error.printStackTrace();
//			}
//
//			@Override
//			public void handleResult(String result) {
//				// TODO Auto-generated method stub
//				System.out.println(result);
//			}
//			
//		}, "bcdesf");
//		System.in.read();
	}

}
