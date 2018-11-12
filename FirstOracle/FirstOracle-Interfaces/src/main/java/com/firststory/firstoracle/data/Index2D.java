/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * @author n1t4chi
 */
public class Index2D extends Index {
    
    public static Index2D id2( Vector2ic vec ) {
        return id2( vec.x(), vec.y() );
    }
    
    public static Index2D id2( int x, int y ) {
        return new Index2D( x, y );
    }
    
    private Index2D( int x, int y ) {
        super( x, y, 0 );
    }
    
    @Override
    public String toString() {
        return "Index2D{ " + x() + ", " + y() + " }";
    }
    
    public Vector2ic toVec3i() {
        return new Vector2i( x(), y() );
    }
}
