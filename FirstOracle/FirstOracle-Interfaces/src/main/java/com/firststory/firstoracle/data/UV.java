/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import org.joml.Vector2fc;

import java.util.Arrays;

/**
 * @author n1t4chi
 */
public class UV implements FloatData {
    
    public static final int U = 0, V = 1;
    
    public static UV uv( Vector2fc vec ) {
        return uv( vec.x(), vec.y() );
    }
    
    public static UV uv( float u, float v ) {
        return new UV( u, v );
    }
    
    private final float[] data = new float[ 2 ];
    
    private UV( float u, float v ) {
        this.data[ U ] = u;
        this.data[ V ] = v;
    }
    
    public float u() {
        return data[ U ];
    }
    
    public float v() {
        return data[ V ];
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode( data );
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
    
        var uv = ( UV ) o;
        
        return Arrays.equals( data, uv.data );
    }
    
    @Override
    public String toString() {
        return "UV{ " + u() + ", " + v() + " }";
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
