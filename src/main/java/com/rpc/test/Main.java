package com.rpc.test;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int hash ="aasa1aa".hashCode();
		System.out.println(hash%16);
		int  segmentShift =28 ;
		int segmentMask = 15;
		System.out.println((hash >>> segmentShift));
		System.out.println((hash >>> segmentShift) & segmentMask);
	}

}
