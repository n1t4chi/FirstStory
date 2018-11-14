/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.Exceptions;

/**
 * @author n1t4chi
 */
public class OrphanedEntryNodeException extends ParseFailedException {
    
    public OrphanedEntryNodeException() {
        super( "Orphaned entry node" );
    }
}
