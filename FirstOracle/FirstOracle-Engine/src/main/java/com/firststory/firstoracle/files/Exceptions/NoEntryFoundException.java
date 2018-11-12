/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files.Exceptions;

import com.firststory.firstoracle.files.structure.Composite;

/**
 * @author n1t4chi
 */
public class NoEntryFoundException extends ParseFailedException {
    
    public NoEntryFoundException( Composite composite, String name ) {
        super( "No entry with name " + name +" found in structure:\n" + composite );
    }
}
