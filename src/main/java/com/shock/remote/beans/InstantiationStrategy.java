package com.shock.remote.beans;

import com.shock.remote.exception.BeanInstantiationException;

import java.lang.reflect.Constructor;

/**
 * Created by shocklee on 16/7/8.
 */
public interface InstantiationStrategy {

    Object instantiate(BeanDefinition bd, String beanName, Constructor<?> ctor, Object... args) throws BeanInstantiationException;



}
