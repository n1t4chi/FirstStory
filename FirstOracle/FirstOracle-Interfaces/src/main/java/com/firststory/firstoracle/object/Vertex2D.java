/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

/**
 * @author n1t4chi
 */
public class Vertex2D extends Vertex {
    
    public static Vertex2D vec2( float x, float y ) {
        return new Vertex2D( x, y );
    }
    
    private Vertex2D( float x, float y ) {
        super( x, y, 0 );
    }
    
    @Override
    public String toString() {
        return "Vertex2D{ " + getX() + ", " + getY() + " }";
    }
}
