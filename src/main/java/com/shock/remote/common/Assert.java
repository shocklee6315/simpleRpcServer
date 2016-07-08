package com.shock.remote.common;

/**
 * Created by shocklee on 16/7/8.
 */
public abstract class Assert {


    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
