/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object.data;

/**
 * @author n1t4chi
 */
public class Position3D extends Position {
    
    public static Position3D pos3( float x, float y, float z ) {
        return new Position3D( x, y, z );
    }
    
    private Position3D( float x, float y, float z ) {
        super( x, y, z );
    }
    
    @Override
    public String toString() {
        return "Position3D{ " + x() + ", " + y() + ", " + z() + " }";
    }
}
