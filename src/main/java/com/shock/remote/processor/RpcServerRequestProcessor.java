package com.shock.remote.processor;

import com.shock.remote.RequestProcessor;
import com.shock.remote.beans.BeanFactory;
import com.shock.remote.body.RpcRequest;
import com.shock.remote.body.RpcResponse;
import com.shock.remote.common.ReflectionUtil;
import com.shock.remote.common.MessageUtil;
import com.shock.remote.common.SerializeUtil;
import com.shock.remote.protocol.RemoteMessage;
import com.shock.remote.protocol.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shocklee on 16/7/4.
 */
public class RpcServerRequestProcessor implements RequestProcessor {

    private  static transient Logger logger = LoggerFactory.getLogger(RpcServerRequestProcessor.class);

    BeanFactory beanFactory ;

    public RpcServerRequestProcessor(BeanFactory container){
        beanFactory = container;
    }

    @Override
    public RemoteMessage processRequest(RemoteMessage request) throws Exception {
        byte[] body =request.getBody();

        if(body!=null && body.length>0){
            RpcRequest rpcRequest =SerializeUtil.protostuffDecode(body, RpcRequest.class);
            RpcResponse rpcResponse =null;
            try {
                rpcResponse = invokeRpc(rpcRequest);

            }catch (Exception ex){
                logger.error("调用rpc服务失败" ,ex);
                return MessageUtil.createResponeMessage(ResponseCode.SYSTEM_ERROR ,request.getMessageId(),MessageUtil.exceptionDesc(ex));
            }

            RemoteMessage response = MessageUtil.createResponeMessage(ResponseCode.SUCCESS, request.getMessageId(), "成功");

            if(rpcResponse !=null) {
                byte[] respbody = SerializeUtil.protostuffEncode(rpcResponse);

                response.setBody(respbody);

            }
            return response;
        }
        return MessageUtil.createResponeMessage(ResponseCode.REQUEST_CODE_NOT_SUPPORTED,request.getMessageId(),"失败");
    }


    private RpcResponse invokeRpc(RpcRequest request) throws Exception {

        Object obj = beanFactory.getBean(ReflectionUtil.forName(request.getClassName()));

        String methodName = request.getMethodName();
        String[] paramTypes = request.getParameterTypes();
        RpcResponse response = new RpcResponse();
        List<Class> parameterTypes = new ArrayList<Class>();
        try {
            for (String s : paramTypes) {
                parameterTypes.add(ReflectionUtil.forName(s));
            }
            Method m = obj.getClass().getMethod(methodName, parameterTypes.toArray(new Class[0]));
            Object rtn = m.invoke(obj,request.getParameters());
            response.setResult(rtn);
        }  catch (ClassNotFoundException e) {
            logger.error("ClassNotFoundException ",e);
            response.setException(e);
        }  catch (IllegalArgumentException|IllegalAccessException|NoSuchMethodException |SecurityException e) {
            logger.error("IllegalArgumentException|IllegalAccessException|NoSuchMethodException |SecurityException ",e);
            response.setException(e);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException ",e);
            response.setException(e.getTargetException());
        }
        return response;
    }

}
