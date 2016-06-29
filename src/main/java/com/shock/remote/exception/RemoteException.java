package com.shock.remote.exception;


/**
 * Created by shocklee on 16/6/29.
 */
public class RemoteException extends Exception{

    private static final long serialVersionUID = -3387516993124229949L;

    public RemoteException(String message ) {
        super(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
