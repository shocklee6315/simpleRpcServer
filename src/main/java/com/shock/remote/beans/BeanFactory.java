package com.shock.remote.beans;

/**
 * Created by shocklee on 16/7/5.
 */
public interface BeanFactory {

    Object getBean(String name) throws Exception;
    <T> T getBean(String name, Class<T> requiredType) throws Exception;
    <T> T getBean(Class<T> requiredType) throws Exception;

}
