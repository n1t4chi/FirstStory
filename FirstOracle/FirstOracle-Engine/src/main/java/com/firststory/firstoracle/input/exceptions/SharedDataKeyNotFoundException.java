/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.exceptions;

/**
 * @author n1t4chi
 */
public class SharedDataKeyNotFoundException extends ParseFailedException {
    
    public SharedDataKeyNotFoundException( String key, String dataType ) {
        super( "Cannot find key " + key + " under " + dataType );
    }
}
