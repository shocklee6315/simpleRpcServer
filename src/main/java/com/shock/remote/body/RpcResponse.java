package com.shock.remote.body;

/**
 * Created by shocklee on 16/7/4.
 */
public class RpcResponse {


    private Throwable exception;

    private Object result;

    public RpcResponse() {
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
                " result: %s, exception: %s", new Object[] {
                         result, exception });
    }
}
