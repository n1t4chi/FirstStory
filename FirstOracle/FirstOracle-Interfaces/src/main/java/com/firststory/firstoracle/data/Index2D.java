/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import org.joml.*;

import java.lang.Math;

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
    
    public static Index2D maxId2( Index2D i1, Index2D i2 ) {
        return Index2D.id2(
            Math.max( i1.x(), i2.x() ),
            Math.max( i1.y(), i2.y() )
        );
    }
    
    public static Index2D minId2( Index2D i1, Index2D i2 ) {
        return Index2D.id2(
            Math.min( i1.x(), i2.x() ),
            Math.min( i1.y(), i2.y() )
        );
    }
    
    private Index2D( int x, int y ) {
        super( x, y, 0 );
    }
    
    public boolean leftFits( Index2D right ) {
        return this.x() <= right.x() && this.y() <= right.y();
    }
    
    public Index2D increment() {
        return Index2D.id2( this.x() + 1, this.y() + 1 );
    }
    
    public boolean belowOrEqual( Index2D minBound ) {
        return this.x() <= minBound.x() || this.y() <= minBound.y();
    }
    
    public Index2D subtract( Index2D index ) {
        return id2(
            this.x() - index.x(),
            this.y() - index.y()
        );
    }
    
    @Override
    public String toString() {
        return "Index2D{ " + x() + ", " + y() + " }";
    }
    
    public Vector2ic toVec3i() {
        return new Vector2i( x(), y() );
    }
}
