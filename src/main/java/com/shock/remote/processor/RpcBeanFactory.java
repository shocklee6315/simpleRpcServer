package com.shock.remote.processor;

import com.shock.remote.beans.BeanDefinition;
import com.shock.remote.beans.BeanFactory;
import com.shock.remote.beans.InstantiationStrategy;
import com.shock.remote.common.Assert;
import com.shock.remote.common.ReflectionUtil;
import com.shock.remote.common.CollectionUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shocklee on 16/7/7.
 */
public class RpcBeanFactory implements BeanFactory{


    private InstantiationStrategy strategy = new SimpleInstantiationStrategy();


    private ClassLoader beanClassLoader = ReflectionUtil.getDefaultClassLoader();

    /**
     *Cache of singleton objects  name --> object
     */
    private final Map factoryBeanObjectCache = CollectionUtil.createConcurrentMap(16);

    private final Map<String ,RpcBeanDefinition> mergedBeanDefinitions = CollectionUtil.createConcurrentMap(16);

    private final Map beanExportClassCache =  CollectionUtil.createConcurrentMap(16);

    /** Names of beans that are currently in creation */
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));


    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    @Override
    public Object getBean(String name) throws Exception {
        if(factoryBeanObjectCache.containsKey(name)){
            return factoryBeanObjectCache.get(name);
        }
        RpcBeanDefinition definition = mergedBeanDefinitions.get(name);
        Assert.notNull(definition,"找不到类定义");
        Object rtn = doGetBean(definition);
        return rtn;
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws Exception {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws Exception {
        return null;
    }

    public Object doGetBean(RpcBeanDefinition definition) {
        if(isSingletonCurrentlyInCreation(definition.getBeanName())){

        }

        return null;
    }


}
