package com.rpc.connector;

/**
 * Created by shocklee on 16/6/26.
 */
public interface Connector {

    public void start();
    public void stop();
    public String getProtocol();
    public int port();

}
