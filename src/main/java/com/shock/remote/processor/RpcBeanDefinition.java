package com.shock.remote.processor;

import com.shock.remote.beans.BeanDefinition;

/**
 * Created by shocklee on 16/7/8.
 */
public class RpcBeanDefinition implements BeanDefinition{

    final Object postProcessingLock = new Object();

    @Override
    public String getBeanClassName() {
        return null;
    }

    @Override
    public void setBeanClassName(String beanClassName) {

    }

    @Override
    public String getBeanName() {
        return null;
    }

    @Override
    public String setBeanName(String name) {
        return null;
    }

    @Override
    public String getFactoryBeanName() {
        return null;
    }

    @Override
    public void setFactoryBeanName(String factoryBeanName) {

    }

    @Override
    public String getFactoryMethodName() {
        return null;
    }

    @Override
    public boolean isLazyInit() {
        return false;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {

    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public String getBeanExportClassName() {
        return null;
    }

    @Override
    public void setBeanExportClassName(String beanClassName) {

    }
}
