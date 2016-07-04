package com.rpc.test;

import java.util.Date;

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
//		throw new RuntimeException("�����ˣ����˸���");
	}

	@Override
	public Po say(Po po) {
		System.out.println(po);
		Po r = new Po();
		r.setAge(11);
		r.setName("hhh");
		r.setTime(new Date());
		return r;
	}

}
