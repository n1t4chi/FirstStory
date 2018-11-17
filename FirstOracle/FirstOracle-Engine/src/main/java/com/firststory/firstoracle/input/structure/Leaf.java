/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.structure;

/**
 * @author n1t4chi
 */
public class Leaf extends Node {
    
    private final String value;
    
    public Leaf( String name, String value ) {
        super( name );
        this.value = removeWhitespaces( value );
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public String toString() {
        return getName() + " -> " + value;
    }
}
