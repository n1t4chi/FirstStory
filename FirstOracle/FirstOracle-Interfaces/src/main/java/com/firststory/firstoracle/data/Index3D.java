/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import org.joml.Vector3i;
import org.joml.Vector3ic;

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
    
    public static Index3D max(
        Index3D i1,
        Index3D i2
    ) {
        return Index3D.id3(
            Math.max( i1.x(), i2.x() ),
            Math.max( i1.y(), i2.y() ),
            Math.max( i1.z(), i2.z() )
        );
    }
    
    public static Index3D increment( Index3D index ) {
        return Index3D.id3( index.x() + 1, index.y() + 1, index.z() + 1 );
    }
    
    public static boolean leftFits(
        Index3D left,
        Index3D right
    ) {
        return left.x() <= right.x() && left.y() <= right.y() && left.z() <= right.z();
    }
    
    private Index3D( int x, int y, int z ) {
        super( x, y, z );
    }
    
    @Override
    public String toString() {
        return "Index3D{ " + x() + ", " + y() + ", " + z() + " }";
    }
    
    public Vector3ic toVec3i() {
        return new Vector3i( x(), y(), z() );
    }
}
