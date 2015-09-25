package com.rpc.test;

import com.rpc.regester.RegestrySupport;
import com.rpc.server.NettyRpcServer;

public class ServerStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final NettyRpcServer server = new NettyRpcServer(8091) ;
		RegestrySupport.getRegestry().regeser(IInterSV.class, new InterSVImpl());
//		new Thread() {
//			
//			public void run() {
//				
//				try {
//					Thread.sleep(10000L);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				server.stop();
//			}
//			
//		}.start();
		server.start();
		
		
	}

}
