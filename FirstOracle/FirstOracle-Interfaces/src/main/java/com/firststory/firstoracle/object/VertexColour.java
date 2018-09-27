/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import java.util.Arrays;

/**
 * @author n1t4chi
 */
public class VertexColour implements FloatData {
    
    public static final int R = 0, G = 1, B = 2, A = 3;
    
    public static VertexColour col( float r, float g, float b, float a ) {
        return new VertexColour( r, g, b, a );
    }
    
    private final float[] data = new float[ 4 ];
    
    private VertexColour( float r, float g, float b, float a ) {
        this.data[ R ] = r;
        this.data[ G ] = g;
        this.data[ B ] = b;
        this.data[ A ] = a;
    }
    
    public float getRed() {
        return data[ R ];
    }
    
    public float getGreen() {
        return data[ G ];
    }
    
    public float getBlue() {
        return data[ B ];
    }
    
    public float getAlpha() {
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
    
        var colour = ( VertexColour ) o;
        
        return Arrays.equals( data, colour.data );
    }
    
    @Override
    public String toString() {
        return "VertexColour{ rgba=( " + getRed() + ", " + getGreen() + ", " + getBlue() + ", " + getAlpha() + " ) }";
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
