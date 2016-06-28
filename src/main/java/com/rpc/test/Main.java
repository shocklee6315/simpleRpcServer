package com.rpc.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		int hash ="aasa1aa".hashCode();
//		System.out.println(hash%16);
//		int  segmentShift =28 ;
//		int segmentMask = 15;
//		System.out.println((hash >>> segmentShift));
//		System.out.println((hash >>> segmentShift) & segmentMask);


		ExecutorService service =new ThreadPoolExecutor(2, 2,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(4));

		for (int k =0 ; k<100;k++){
			service.submit(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("启动");
						Thread.sleep(100L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		service.shutdown();
	}

}
