package com.shock.remote.body;

import java.util.UUID;

/**
 * Created by shocklee on 16/7/4.
 */
public class RpcRequest {

    private String className;
    private String methodName;
    private String[] parameterTypes;
    private Object[] parameters;

    public RpcRequest() {

    }

    public RpcRequest(String className, String methodName,
                      String[] parameterTypes, Object[] parameters) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
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
        return String.format(" className: %s, methodName: %s, parameterTypes: %s, parameters: %s",
                new Object[] {  className, methodName,
                        parameterTypes, parameters });
    }
}
