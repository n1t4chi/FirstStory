/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object.data;

import java.util.Arrays;

/**
 * @author n1t4chi
 */
public class Position implements FloatData {
    
    public static final int X = 0, Y = 1, Z = 2;
    private final float[] data = new float[ 3 ];
    
    public Position( float x, float y, float z ) {
        this.data[ X ] = x;
        this.data[ Y ] = y;
        this.data[ Z ] = z;
    }
    
    public float x() {
        return data[ X ];
    }
    
    public float y() {
        return data[ Y ];
    }
    
    public float z() {
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
        
        var position = ( Position ) o;
        
        return Arrays.equals( data, position.data );
    }
    
    @Override
    public String toString() {
        return "Position{ " + x() + ", " + y() + ", " + z() + " }";
    }
    
    @Override
    public int size() {
        return data.length;
    }
    
    @Override
    public float[] data() {
        return data;
    }
}
