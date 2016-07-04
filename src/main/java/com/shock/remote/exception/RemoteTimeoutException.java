package com.shock.remote.exception;

/**
 * Created by shocklee on 16/6/30.
 */
public class RemoteTimeoutException extends RemoteException{

    public static final long serialVersionUID = -3387516993124229950L;

    public RemoteTimeoutException(String message) {
        super(message);
    }


    public RemoteTimeoutException(String addr, long timeoutMillis) {
        this(addr, timeoutMillis, null);
    }


    public RemoteTimeoutException(String addr, long timeoutMillis, Throwable cause) {
        super("wait response on the channel <" + addr + "> timeout, " + timeoutMillis + "(ms)", cause);
    }

}
