package com.rpc.serializer;

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
