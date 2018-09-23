/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

/**
 * @author n1t4chi
 */
public class Vertex3D extends Vertex {
    
    public static Vertex3D vec3( float x, float y, float z ) {
        return new Vertex3D( x, y, z );
    }
    
    private Vertex3D( float x, float y, float z ) {
        super( x, y, z );
    }
    
    @Override
    public String toString() {
        return "Vertex3D{ " + getX() + ", " + getY() + ", " + getZ() + " }";
    }
}
