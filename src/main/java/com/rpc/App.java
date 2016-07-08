package com.rpc;

import com.shock.remote.common.JdkVersion;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        System.out.println(JdkVersion.isAtLeastJava18());
    }
}
