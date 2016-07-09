package com.shock.remote.processor;

import com.shock.remote.beans.BeanDefinition;
import com.shock.remote.beans.InstantiationStrategy;
import com.shock.remote.common.ReflectionUtil;
import com.shock.remote.exception.BeanInstantiationException;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

/**
 * Created by shocklee on 16/7/8.
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy{
    @Override
    public Object instantiate(BeanDefinition bd, String beanName, Constructor<?> ctor, Object... args) throws BeanInstantiationException {

        if (System.getSecurityManager() != null) {
            // use own privileged to change accessibility (when security is on)
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    ReflectionUtil.makeAccessible(ctor);
                    return null;
                }
            });
        }
        return ReflectionUtil.instantiateClass(ctor,args);
    }


    public Object instantiate(RpcBeanDefinition bd, String beanName) throws BeanInstantiationException {
        Constructor<?> constructorToUse;
        constructorToUse = (Constructor<?>) bd.getConstructor();
        if (constructorToUse == null) {
            try {
                constructorToUse = bd.resolveConstructor();
            } catch (ClassNotFoundException e) {
                throw new BeanInstantiationException(bd.getBeanClass(), "can not find an constructor ");
            }
        }
        if (constructorToUse !=null)
            return ReflectionUtil.instantiateClass(constructorToUse);

        return null;
    }
}
