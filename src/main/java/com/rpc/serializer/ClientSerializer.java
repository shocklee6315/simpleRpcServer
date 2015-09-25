package com.rpc.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ClientSerializer {
	/**
	 * deserialize the inputStream
	 * 
	 * @param inputStream
	 * @return
	 * @throws SerializeException
	 * @throws IOException
	 */
	RpcResponse decodeResponse(InputStream inputStream)
			throws SerializerException, IOException;

	/**
	 * serialize the request object into the outputStream
	 * 
	 * @param outputStream
	 * @param object
	 * @param method
	 * @param arguments
	 * @throws SerializeException
	 * @throws IOException
	 */
	void encodeRequest(OutputStream outputStream, RpcRequest request)
			throws SerializerException, IOException;
}