package com.shock.remote.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by shocklee on 16/7/7.
 */
public abstract class CollectionUtil {

    private  static transient Logger logger = LoggerFactory.getLogger(CollectionUtil.class);

    public static Map createConcurrentMap(int initialCapacity) {

        if (JdkVersion.isAtLeastJava15()) {
            logger.trace("Creating [java.util.concurrent.ConcurrentHashMap]");
            return new ConcurrentHashMap(initialCapacity);
        }
        else {
            logger.debug("Falling back to plain synchronized [java.util.HashMap] for concurrent map");
            return Collections.synchronizedMap(new HashMap(initialCapacity));
        }
    }

    public static Set createLinkedSetIfPossible(int initialCapacity) {
        return new LinkedHashSet(initialCapacity);
    }

    public static Set createCopyOnWriteSet() {
        if (JdkVersion.isAtLeastJava15()) {
            logger.trace("Creating [java.util.concurrent.CopyOnWriteArraySet]");
            return new CopyOnWriteArraySet();
        }else {
            throw new IllegalStateException("Cannot create CopyOnWriteArraySet - " +
                    "neither JDK 1.5 nor backport-concurrent available on the classpath");
        }
    }

}
