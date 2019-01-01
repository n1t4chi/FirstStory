/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

/**
 * @author n1t4chi
 */
class Bounds {
    
    private final int min;
    private final int max;
    
    Bounds( int min, int max ) {
        this.min = min;
        this.max = max;
    }
    
    int getMin() {
        return min;
    }
    
    int getMax() {
        return max;
    }
    
    @Override
    public String toString() {
        return "Bounds( " + min + ", " + max + " )";
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        
        var bounds = ( Bounds ) o;
        
        if ( min != bounds.min ) { return false; }
        return max == bounds.max;
    }
    
    @Override
    public int hashCode() {
        int result = min;
        result = 31 * result + max;
        return result;
    }
}
