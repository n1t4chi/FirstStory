/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.exceptions;

/**
 * @author n1t4chi
 */
public class ParsedClassNotFoundException extends RuntimeException {
    
    public ParsedClassNotFoundException(
        String className,
        Class< ? > superClass,
        Exception ex
    ) {
        super( "No class with name " + className + " was found that inherits from " + superClass.getName(), ex );
    }
}
