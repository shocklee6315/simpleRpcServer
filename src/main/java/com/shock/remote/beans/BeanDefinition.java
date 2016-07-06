package com.shock.remote.beans;

/**
 * Created by shocklee on 16/7/5.
 */
public interface BeanDefinition {
    String getBeanClassName();
    void setBeanClassName(String beanClassName);
    String getFactoryBeanName();
    void setFactoryBeanName(String factoryBeanName);
    String getFactoryMethodName();
    boolean isLazyInit();
    void setLazyInit(boolean lazyInit);
    boolean isSingleton();

}
