package com.shock.remote.common;

import com.shock.remote.protocol.RemoteMessage;

/**
 * Created by shocklee on 16/6/28.
 */
public final class MessageUtil {


    public static RemoteMessage createResponeMessage(int code , String messageId , String remarks ){
        RemoteMessage response = new RemoteMessage(messageId);
        response.markResponseType();
        response.setRtnCode(code);
        response.setRemarks(remarks);
        response.setVersion(1);
        return response;
    }

    public static String exceptionDesc(final Throwable e) {
        StringBuffer sb = new StringBuffer();
        if (e != null) {
            sb.append(e.toString());
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                StackTraceElement elment = stackTrace[0];
                sb.append(", ");
                sb.append(elment.toString());
            }
        }

        return sb.toString();
    }

}
