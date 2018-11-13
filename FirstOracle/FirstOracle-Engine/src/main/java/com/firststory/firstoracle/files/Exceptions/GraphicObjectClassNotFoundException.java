/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files.Exceptions;

/**
 * @author n1t4chi
 */
public class GraphicObjectClassNotFoundException extends RuntimeException {
    
    public GraphicObjectClassNotFoundException(
        String className,
        Class< ? > superClass,
        Exception ex
    ) {
        super( "No class with name " + className + " was found that inherits from " + superClass.getName(), ex );
    }
}
