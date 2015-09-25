package com.rpc.test;

import java.util.Random;

import com.rpc.client.Client;

public class RpcClientTest {

	public static void main(String[] args) throws Exception {

		final Client client = new Client("tcp://localhost:8091");
		final IInterSV in = client.getProxy(IInterSV.class);
		in.sayHello("102992����" );
		for (int i = 0; i < 1; i++)
			new Thread() {
				public void run() {
					int name = new Random().nextInt();
					long start = System.currentTimeMillis();
//					String as =in.hello("102992����" + name);
					
					long end = System.currentTimeMillis();
					
//					System.out.println("���index =" + name + " �ķ��ؽ���� "
//							+ as);
					;
					for(int i =0 ; i<1 ;i++){
						in.sayHello("102992����" + name);
					}
					end = System.currentTimeMillis();
					System.out.println("��ʱ��"+(end - start));
					try {
						in.exception("102992����" + name);
						
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}.start();
//		client.destory();
	}
}
