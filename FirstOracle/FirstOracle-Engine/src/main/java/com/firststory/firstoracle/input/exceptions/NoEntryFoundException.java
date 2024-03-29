/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.exceptions;

import com.firststory.firstoracle.input.structure.Composite;

/**
 * @author n1t4chi
 */
public class NoEntryFoundException extends ParseFailedException {
    
    public NoEntryFoundException( Composite composite, String name ) {
        super( "No entry with name " + name +" found in structure:\n" + composite );
    }
}
