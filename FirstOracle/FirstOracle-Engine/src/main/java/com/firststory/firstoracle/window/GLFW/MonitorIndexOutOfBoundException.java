/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.GLFW;

public class MonitorIndexOutOfBoundException extends RuntimeException {
    
    public MonitorIndexOutOfBoundException( int monitorIndex, int capacity ){
        super("Selected monitor index is out of bounds. Provided index: "+monitorIndex+". Last index:"+capacity+".");
    }
}
