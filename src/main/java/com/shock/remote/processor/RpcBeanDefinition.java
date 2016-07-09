package com.shock.remote.processor;

import com.shock.remote.beans.BeanDefinition;
import com.shock.remote.common.ReflectionUtil;
import com.shock.remote.exception.BeanInstantiationException;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * Created by shocklee on 16/7/8.
 */
public class RpcBeanDefinition implements BeanDefinition{

    final Object postProcessingLock = new Object();

    Class beanClass ;

    Constructor constructor ;

    private  String beanClassName ;
    private String beanName;

    private String factoryBeanName;

    private String factoryMethod;

    private boolean isLazy ;

    private String beanExportClassName;
    @Override
    public String getBeanClassName() {
        return this.beanClassName;
    }

    @Override
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    @Override
    public String getBeanName() {
        return this.beanName;
    }

    @Override
    public String setBeanName(String name) {
        this.beanName = name;
        return this.beanName;
    }

    @Override
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Override
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    @Override
    public String getFactoryMethodName() {
        return this.factoryMethod;
    }

    @Override
    public boolean isLazyInit() {
        return this.isLazy;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.isLazy = lazyInit;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public String getBeanExportClassName() {
        return beanExportClassName;
    }

    @Override
    public void setBeanExportClassName(String beanClassName) {
        this.beanExportClassName = beanClassName;
    }

    public boolean hasBeanClass(){
        return (this.beanClass instanceof Class );
    }


    public Class<?> getBeanClass(){
        return beanClass;
    }

    public Class<?> resolveBeanClass(ClassLoader classLoader) throws ClassNotFoundException {
        String className = getBeanClassName();
        if (className == null) {
            return null;
        }
        Class<?> resolvedClass = ReflectionUtil.forName(className);
        this.beanClass = resolvedClass;
        return resolvedClass;
    }

    public Constructor<?> getConstructor(){
        return this.constructor;
    }
    public Constructor<?> resolveConstructor() throws ClassNotFoundException{
        if (! hasBeanClass()){
            resolveBeanClass(ReflectionUtil.getDefaultClassLoader());
        }
        final Class<?> clazz = this.getBeanClass();
        Constructor<?> constructorToUse;
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            if (System.getSecurityManager() != null) {
                constructorToUse = AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor<?>>() {
                    @Override
                    public Constructor<?> run() throws Exception {
                        return clazz.getDeclaredConstructor((Class[]) null);
                    }
                });
            } else {
                constructorToUse = clazz.getDeclaredConstructor((Class[]) null);
            }
        } catch (Exception ex) {
            throw new BeanInstantiationException(clazz, "No default constructor found", ex);
        }
        this.constructor = constructorToUse;
        return constructorToUse;
    }

}
