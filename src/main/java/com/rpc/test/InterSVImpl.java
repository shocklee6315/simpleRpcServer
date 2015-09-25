package com.rpc.test;

public class InterSVImpl implements IInterSV{

	@Override
	public String hello(String name) {
		// TODO Auto-generated method stub
		return "hell do you say " + name;
	}

	@Override
	public void sayHello(String name) {
		System.out.println(name);
	}

	@Override
	public void exception(String name) {
		// TODO Auto-generated method stub
		System.out.println(name);
		throw new RuntimeException("杯具了，抛了个错！");
	}

}
