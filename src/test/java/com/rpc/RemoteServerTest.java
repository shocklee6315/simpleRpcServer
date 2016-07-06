package com.rpc;

import com.rpc.test.InterSVImpl;
import com.shock.remote.RequestProcessor;
import com.shock.remote.beans.BeanFactory;
import com.shock.remote.processor.RpcServerRequestProcessor;
import com.shock.remote.server.NettyRemoteServer;
import com.shock.remote.server.NettyServerConfig;
import com.shock.remote.protocol.RemoteMessage;

/**
 * Created by shocklee on 16/6/27.
 */
public class RemoteServerTest {
    public static void main(String[] args) throws Exception{
        NettyServerConfig config = new NettyServerConfig();
        config.setListenPort(8081);
        config.setServerWorkerThreads(16);
        config.setServerMaxConcurrencyRequest(200);
        NettyRemoteServer server = new NettyRemoteServer(config);
//        RequestProcessor processor = new RequestProcessor(){
//
//            @Override
//            public RemoteMessage processRequest(RemoteMessage request) throws Exception {
//                RemoteMessage response = new RemoteMessage();
//                response.setVersion(1);
//                response.setRemarks("测试消息");
//                //试试耗时的拒绝消息
//                Thread.currentThread().sleep(1L);
//                return response;
//            }
//        };
        BeanFactory factory = new BeanFactory(){

            @Override
            public Object getBean(String name) throws Exception {
                System.out.println(name);
                return new InterSVImpl();
            }
            @Override
            public <T> T getBean(String name, Class<T> requiredType) throws Exception {
                return requiredType.newInstance();
            }
            @Override
            public <T> T getBean(Class<T> requiredType) throws Exception {
                System.out.println(requiredType.getName());
                return requiredType.newInstance();
            }
        };
        server.regesterRequestProcessor(new RpcServerRequestProcessor(factory));
        server.start();
        System.in.read();
    }
}
