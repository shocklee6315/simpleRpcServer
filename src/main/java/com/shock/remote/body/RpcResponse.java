package com.shock.remote.body;

/**
 * Created by shocklee on 16/7/4.
 */
public class RpcResponse {

    private String requestID;

    private Throwable exception;

    private Object result;

    public RpcResponse() {
    }

    public RpcResponse(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return String.format(
                "requestID: %s, result: %s, exception: %s", new Object[] {
                        requestID, result, exception });
    }
}
