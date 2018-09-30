/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object.data;

/**
 * @author n1t4chi
 */
public class Rotation3D extends Position {
    
    public static Rotation3D rot3( float x, float y, float z ) {
        return new Rotation3D( x, y, z );
    }
    
    private Rotation3D( float x, float y, float z ) {
        super( x, y, z );
    }
    
    @Override
    public String toString() {
        return "Rotation3D{ " + x() + ", " + y() + ", " + z() + " }";
    }
}
