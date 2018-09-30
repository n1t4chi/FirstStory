/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object.data;

import java.util.Arrays;

/**
 * @author n1t4chi
 */
public class Rotation implements FloatData {
    
    public static final int OX = 0, OY = 1, OZ = 2;
    private final float[] data = new float[ 3 ];
    
    public Rotation( float OX, float OY, float OZ ) {
        this.data[ Rotation.OX ] = OX;
        this.data[ Rotation.OY ] = OY;
        this.data[ Rotation.OZ ] = OZ;
    }
    
    public float ox() {
        return data[ OX ];
    }
    
    public float oy() {
        return data[ OY ];
    }
    
    public float oz() {
        return data[ OZ ];
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode( data );
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        
        var rotation = ( Rotation ) o;
        
        return Arrays.equals( data, rotation.data );
    }
    
    @Override
    public String toString() {
        return "Rotation{ " + ox() + ", " + oy() + ", " + oz() + " }";
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
