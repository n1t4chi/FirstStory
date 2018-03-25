/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.GLFW;

/**
 * @author n1t4chi
 */
public class CannotCreateWindowException extends RuntimeException {
    
    public CannotCreateWindowException() {
        super( "Failed to create the GLFW window" );
    }
}
