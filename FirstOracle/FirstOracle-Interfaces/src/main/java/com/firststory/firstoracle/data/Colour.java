/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import java.util.Arrays;

/**
 * @author n1t4chi
 */
public class Colour implements FloatData {
    
    public static final int R = 0, G = 1, B = 2, A = 3;
    
    public static Colour col( float r, float g, float b, float a ) {
        return new Colour( r, g, b, a );
    }
    
    private final float[] data = new float[ 4 ];
    
    private Colour( float r, float g, float b, float a ) {
        this.data[ A ] = a;
        this.data[ G ] = g;
        this.data[ B ] = b;
        this.data[ R ] = r;
    }
    
    public float red() {
        return data[ R ];
    }
    
    public float green() {
        return data[ G ];
    }
    
    public float blue() {
        return data[ B ];
    }
    
    public float alpha() {
        return data[ A ];
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode( data );
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        
        var colour = ( Colour ) o;
        
        return Arrays.equals( data, colour.data );
    }
    
    @Override
    public String toString() {
        return "Colour{ rgba=( " + red() + ", " + green() + ", " + blue() + ", " + alpha() + " ) }";
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
