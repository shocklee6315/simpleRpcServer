package com.rpc.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ServerSerializer {
	/**
	 * deserialize the inputStream
	 * 
	 * @param inputStream
	 * @return
	 * @throws SerializeException
	 * @throws IOException
	 */
	RpcRequest decodeRequest(InputStream inputStream)
			throws SerializerException, IOException;

	/**
	 * serialize the result object into the outputStream
	 * 
	 * @param outputStream
	 * @param result
	 * @throws SerializeException
	 * @throws IOException
	 */
	void encodeResponse(OutputStream outputStream, RpcResponse result)
			throws SerializerException, IOException;
}
