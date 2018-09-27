/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import java.util.Arrays;

/**
 * @author n1t4chi
 */
public class Vertex implements FloatData {
    public static final int X=0, Y=1, Z=2;
    private final float[] data = new float[3];
    
    public Vertex( float x, float y, float z ) {
        this.data[X] = x;
        this.data[Y] = y;
        this.data[Z] = z;
    }
    
    public float getX() {
        return data[X];
    }
    
    public float getY() {
        return data[Y];
    }
    
    public float getZ() {
        return data[Z];
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
    
        var vertex = ( Vertex ) o;
    
        return Arrays.equals( data, vertex.data );
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode( data );
    }
    
    @Override
    public int size() {
        return data.length;
    }
    
    @Override
    public float[] data() {
        return data;
    }
    
    @Override
    public String toString() {
        return "Vertex{ " + getX() + ", " + getY() + ", " + getZ() + " }";
    }
}
