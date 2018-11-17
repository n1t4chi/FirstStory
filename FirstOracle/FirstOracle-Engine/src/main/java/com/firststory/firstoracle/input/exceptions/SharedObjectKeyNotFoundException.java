/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.exceptions;

/**
 * @author n1t4chi
 */
public class SharedObjectKeyNotFoundException extends ParseFailedException {
    
    public SharedObjectKeyNotFoundException( String key, String objectType ) {
        super( "Cannot find key " + key + " under " + objectType );
    }
}
