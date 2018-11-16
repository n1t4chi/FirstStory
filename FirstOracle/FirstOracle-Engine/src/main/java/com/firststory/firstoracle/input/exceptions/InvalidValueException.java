/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.exceptions;

/**
 * @author n1t4chi
 */
public class InvalidValueException extends RuntimeException {
    
    public InvalidValueException( String value, String expectedType ) {
        super( "Cannot parse '"+value+"', expected " + expectedType+" type of value." );
    }
}
