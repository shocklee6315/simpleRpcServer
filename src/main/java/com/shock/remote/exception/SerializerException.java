package com.shock.remote.exception;

/**
 * Created by shocklee on 16/6/30.
 */
public class SerializerException extends RuntimeException {

    private static final long serialVersionUID = -1L;

    public SerializerException() {
        super();
    }

    public SerializerException(String msg) {
        super(msg);
    }

    public SerializerException(Throwable t) {
        super(t);
    }

    public SerializerException(String msg, Throwable t) {
        super(msg, t);
    }
}
