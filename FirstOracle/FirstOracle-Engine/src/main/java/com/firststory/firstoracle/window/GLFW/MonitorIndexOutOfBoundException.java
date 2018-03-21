/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.GLFW;

class MonitorIndexOutOfBoundException extends RuntimeException {
    
    MonitorIndexOutOfBoundException( int monitorIndex, int capacity ){
        super("Selected monitor index is out of bounds. Provided index: "+monitorIndex+". Last index:"+capacity+".");
    }
}
