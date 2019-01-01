/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import org.joml.*;

import java.lang.Math;

/**
 * @author n1t4chi
 */
public class Index3D extends Index {
    
    public static Index3D id3( Vector3ic vec ) {
        return id3( vec.x(), vec.y(), vec.z() );
    }
    
    public static Index3D id3( int x, int y, int z ) {
        return new Index3D( x, y, z );
    }
    
    public static Index3D maxId3( Index3D i1, Index3D i2 ) {
        return Index3D.id3(
            Math.max( i1.x(), i2.x() ),
            Math.max( i1.y(), i2.y() ),
            Math.max( i1.z(), i2.z() )
        );
    }
    
    public static Index3D minId3( Index3D i1, Index3D i2 ) {
        return Index3D.id3(
            Math.min( i1.x(), i2.x() ),
            Math.min( i1.y(), i2.y() ),
            Math.min( i1.z(), i2.z() )
        );
    }
    
    private Index3D( int x, int y, int z ) {
        super( x, y, z );
    }
    
    public Index3D increment() {
        return Index3D.id3( this.x() + 1, this.y() + 1, this.z() + 1 );
    }
    
    public boolean leftFits( Index3D right ) {
        return this.x() <= right.x() && this.y() <= right.y() && this.z() <= right.z();
    }
    
    public boolean belowOrEqual( Index3D minBound ) {
        return this.x() <= minBound.x() || this.y() <= minBound.y() || this.z() <= minBound.z();
    }
    
    public Index3D subtract( Index3D index ) {
        return id3(
            this.x() - index.x(),
            this.y() - index.y(),
            this.z() - index.z()
        );
    }
    
    @Override
    public String toString() {
        return "Index3D{ " + x() + ", " + y() + ", " + z() + " }";
    }
    
    public Vector3ic toVec3i() {
        return new Vector3i( x(), y(), z() );
    }
}
