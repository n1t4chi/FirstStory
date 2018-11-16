/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.exceptions;

/**
 * @author n1t4chi
 */
public class NoMatchingEndTagException extends ParseFailedException {
    
    public NoMatchingEndTagException() {
        super( "No matching end tag" );
    }
}
