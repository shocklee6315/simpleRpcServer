package com.shock.remote.processor;

import com.shock.remote.beans.BeanDefinition;
import com.shock.remote.beans.BeanFactory;
import com.shock.remote.beans.InstantiationStrategy;
import com.shock.remote.common.Assert;
import com.shock.remote.common.ReflectionUtil;
import com.shock.remote.common.CollectionUtil;

import java.lang.reflect.Constructor;
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

    private final Map<String ,String> beanExportClassCache =  CollectionUtil.createConcurrentMap(16);

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
        if(factoryBeanObjectCache.containsKey(name)){
            return (T) factoryBeanObjectCache.get(name);
        }
        RpcBeanDefinition definition = mergedBeanDefinitions.get(name);
        if(definition ==null){
            String destName = beanExportClassCache.get(requiredType.getName());
            if (destName!=null ){
                definition = mergedBeanDefinitions.get(destName);
            }
        }
        Assert.notNull(definition,"找不到类定义");
        return (T ) doGetBean(definition);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws Exception {
        String destName = beanExportClassCache.get(requiredType.getName());
        Assert.notNull(destName,"找不到类定义");
        RpcBeanDefinition definition = mergedBeanDefinitions.get(destName);
        Assert.notNull(definition,"找不到类定义");
        return (T ) doGetBean(definition);
    }

    public Object doGetBean(RpcBeanDefinition definition) throws Exception{
        if(factoryBeanObjectCache.containsKey(definition.getBeanName())){
            return factoryBeanObjectCache.get(definition.getBeanName());
        }
        if(isSingletonCurrentlyInCreation(definition.getBeanName())){
            synchronized (definition.postProcessingLock){
                Object rtn =factoryBeanObjectCache.get(definition.getBeanName());
                Assert.notNull(rtn,"类初始化失败");
            }
        }else{
            synchronized (definition.postProcessingLock){
                if(isSingletonCurrentlyInCreation(definition.getBeanName())){
                    throw new RuntimeException("出现并发构建请稍后再试");
                }
                singletonsCurrentlyInCreation.add(definition.getBeanName());
                try {
                    Class clz =null;
                    if(definition.getBeanClass()!=null){
                        clz = definition.getBeanClass();
                    }else{
                        clz=definition.resolveBeanClass(beanClassLoader);
                    }
                    Constructor constructor =clz.getDeclaredConstructor(new Class[0]);
                    Object objRtn =strategy.instantiate(definition ,definition.getBeanName(),constructor);
                    factoryBeanObjectCache.put(definition.getBeanName() ,objRtn);
                    return objRtn;
                }finally {
                    singletonsCurrentlyInCreation.remove(definition.getBeanName());
                }
            }

        }

        return null;
    }


    public void scanBeanDefinion(){
        // TODO: 16/7/9
    }

    public void addBeanDefinion(RpcBeanDefinition definition){
        this.mergedBeanDefinitions.put(definition.getBeanName(),definition);
        this.beanExportClassCache.put(definition.getBeanExportClassName(),definition.getBeanName());

    }

}
