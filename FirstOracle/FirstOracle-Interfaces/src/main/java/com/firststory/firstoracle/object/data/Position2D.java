/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object.data;

/**
 * @author n1t4chi
 */
public class Position2D extends Position {
    
    public static Position2D pos2( float x, float y ) {
        return new Position2D( x, y );
    }
    
    private Position2D( float x, float y ) {
        super( x, y, 0 );
    }
    
    @Override
    public String toString() {
        return "Position2D{ " + x() + ", " + y() + " }";
    }
}
