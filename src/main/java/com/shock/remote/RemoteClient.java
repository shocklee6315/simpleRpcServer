package com.shock.remote;

import com.shock.remote.protocol.RemoteMessage;

import java.util.List;

/**
 * Created by shocklee on 16/6/29.
 */
public interface RemoteClient {


    public void start();

    public void stop();

    public void updateServerAddressList(List<String> addrs) ;

    public List<String> getServerAddressList();

    public RemoteMessage invokeSync(String addr , RemoteMessage request , long timeoutMilis) throws Exception;

    public void invokeASync(String addr , RemoteMessage request , long timeoutMilis , InvokeCallback callback) throws Exception;


}
