/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.Exceptions;

/**
 * @author n1t4chi
 */
public class ParseFailedException extends RuntimeException {
    
    private static final String MESSAGE = "Cannot parse";
    private static final String MESSAGE_PREFIX = MESSAGE + ": ";
    
    public ParseFailedException( Exception ex ) {
        super( MESSAGE, ex );
    }
    
    public ParseFailedException( String s ) {
        super( MESSAGE_PREFIX + s );
    }
    
    public ParseFailedException( String s, Exception ex ) {
        super( MESSAGE_PREFIX + s, ex );
    }
}
