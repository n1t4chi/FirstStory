/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object.data;

import com.firststory.firstoracle.object.IntData;

import java.util.Arrays;

/**
 * @author n1t4chi
 */
public class Index implements IntData {
    
    public static final int X = 0, Y = 1, Z = 2;
    private final int[] data = new int[ 3 ];
    
    public Index( int x, int y, int z ) {
        this.data[ X ] = x;
        this.data[ Y ] = y;
        this.data[ Z ] = z;
    }
    
    public int x() {
        return data[ X ];
    }
    
    public int y() {
        return data[ Y ];
    }
    
    public int z() {
        return data[ Z ];
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode( data );
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        
        var position = ( Index ) o;
        
        return Arrays.equals( data, position.data );
    }
    
    @Override
    public String toString() {
        return "Index{ " + x() + ", " + y() + ", " + z() + " }";
    }
    
    @Override
    public int size() {
        return data.length;
    }
    
    @Override
    public int[] data() {
        return data;
    }
}
