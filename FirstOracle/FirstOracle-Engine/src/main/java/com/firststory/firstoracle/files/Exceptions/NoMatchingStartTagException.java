/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files.Exceptions;

/**
 * @author n1t4chi
 */
public class NoMatchingStartTagException extends ParseFailedException {
    
    public NoMatchingStartTagException() {
        super( "No matching start tag" );
    }
}
