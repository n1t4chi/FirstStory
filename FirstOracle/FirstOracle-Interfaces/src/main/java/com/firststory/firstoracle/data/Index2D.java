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
    
    public static Index2D max(
        Index2D i1,
        Index2D i2
    ) {
        return Index2D.id2(
            Math.max( i1.x(), i2.x() ),
            Math.max( i1.y(), i2.y() )
        );
    }
    
    public static Index2D increment( Index2D index ) {
        return Index2D.id2( index.x() + 1, index.y() + 1 );
    }
    
    public static boolean leftFits(
        Index2D left,
        Index2D right
    ) {
        return left.x() <= right.x() && left.y() <= right.y();
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
