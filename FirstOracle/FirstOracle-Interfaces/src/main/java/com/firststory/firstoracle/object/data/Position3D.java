/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object.data;

import org.joml.Vector3f;
import org.joml.Vector3fc;

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
    
    public Vector3fc toVec3f() {
        return new Vector3f( x(), y(), z() );
    }
}
