/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files.structure;

/**
 * @author n1t4chi
 */
public abstract class Node {
    
    private final String name;
    
    Node( String name ) {
        this.name = removeWhitespaces( name );
    }
    
    public String getName() {
        return name;
    }
    
    public abstract boolean isComposite();
    
    String removeWhitespaces( String string ) {
        return string.replaceAll( "\\s+", " " ).strip();
    }
}
