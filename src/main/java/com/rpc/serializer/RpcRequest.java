package com.rpc.serializer;

import java.util.UUID;


public class RpcRequest {

	private String requestID;
	private String className;
	private String methodName;
	private String[] parameterTypes;
	private Object[] parameters;

	public RpcRequest() {
		this.requestID =UUID.randomUUID().toString();
	}

	public RpcRequest(String className, String methodName,
			String[] parameterTypes, Object[] parameters) {
		this.requestID =UUID.randomUUID().toString();
		this.className = className;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return String.format("requestID: %s, className: %s, methodName: %s, parameterTypes: %s, parameters: %s",
						new Object[] { requestID, className, methodName,
								parameterTypes, parameters });
	}
}
