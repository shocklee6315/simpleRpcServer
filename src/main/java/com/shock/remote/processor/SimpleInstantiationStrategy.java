package com.shock.remote.processor;

import com.shock.remote.beans.BeanDefinition;
import com.shock.remote.beans.InstantiationStrategy;
import com.shock.remote.exception.BeanInstantiationException;

import java.lang.reflect.Constructor;

/**
 * Created by shocklee on 16/7/8.
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy{
    @Override
    public Object instantiate(BeanDefinition bd, String beanName, Constructor<?> ctor, Object... args) throws BeanInstantiationException {
        return null;
    }
}
