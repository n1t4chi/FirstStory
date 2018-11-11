/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import org.joml.Vector3fc;

import java.util.function.Function;

import static com.firststory.firstoracle.FirstOracleConstants.isReallyClose;
import static com.firststory.firstoracle.FirstOracleConstants.vec3ToStr;

/**
 * @author n1t4chi
 */
class Plane {
    private final Vector3fc highLeft;
    private final Vector3fc right;
    private final Vector3fc lowRight;
    private final Vector3fc left;
    private final Function< Vector3fc , Float > dim1;
    private final Function< Vector3fc , Float > dim2;
    
    static Plane planeXY(
        Vector3fc highLeft,
        Vector3fc right,
        Vector3fc lowRight,
        Vector3fc left
    ) {
        return new Plane( highLeft, right, lowRight, left, Vector3fc::x, Vector3fc::y );
    }
    static Plane planeXZ(
        Vector3fc highLeft,
        Vector3fc right,
        Vector3fc lowRight,
        Vector3fc left
    ) {
        return new Plane( highLeft, right, lowRight, left, Vector3fc::x, Vector3fc::z );
    }
    
    Plane(
        Vector3fc highLeft, 
        Vector3fc right, 
        Vector3fc lowRight, 
        Vector3fc left, 
        Function< Vector3fc, Float > dim1, 
        Function< Vector3fc, Float > dim2
    ) {
        this.highLeft = highLeft;
        this.right = right;
        this.lowRight = lowRight;
        this.left = left;
        this.dim1 = dim1;
        this.dim2 = dim2;
    }
    
    public Bounds getBoundsAtDim1( float d1 ) {
        if( d1 < minDim1_Floor() ) {
            return newBounds( 0, 0 );
        }
        if( d1 > maxDim1_Ceil() ) {
            return newBounds( 0, 0 );
        }
        
        var isRightToHightLeft = isToRight( d1, dim1.apply( highLeft ) );
        var maxD2 = findDim2( d1, highLeft, isRightToHightLeft ? right : left, isRightToHightLeft, true );
        
        var isRightToLowRight = isToRight( d1, dim1.apply( lowRight ) );
        var minD2 = findDim2( d1, lowRight, isRightToLowRight ? right : left, isRightToLowRight, false  );
        
        return newBounds( minD2, maxD2 );
    }
    
    private float findDim2( float d1, Vector3fc vec1, Vector3fc vec2, boolean isRight, boolean isUpper ) {
        var dd1 = dim1.apply( vec1 ) - dim1.apply( vec2 );
        var shiftY = 0f;
        if( !isReallyClose( dd1, 0 ) ) {
            var dd2 = dim2.apply( vec1 ) - dim2.apply( vec2 );

            var shiftD1 = d1 - dim1.apply( vec1 );
            var mul = shiftD1 / dd1;
            shiftY = dd2 * mul;
        }
        var d2 = dim2.apply( vec1 ) + shiftY;
        
        if( isRight && d1 > dim1.apply( vec2 ) || !isRight && d1 < dim1.apply( vec2 ) ) {
            if ( isUpper && d2 < dim2.apply( vec2 ) ) {
                d2 = dim2.apply( vec2 );
            } else if ( !isUpper && d2 > dim2.apply( vec2 ) ) {
                d2 = dim2.apply( vec2 );
            }
        }
        
        return d2;
    }
    
    private boolean isToRight( float x, float x2 ) {
        return x > x2;
    }
    
    private Bounds newBounds( float minY, float maxY ) {
        return new Bounds( ( int ) Math.floor( minY ), ( int ) Math.ceil( maxY ) );
    }
    
    int minDim1_Floor() {
        return ( int ) Math.floor( dim1.apply( left ) );
    }
    
    int maxDim1_Ceil() {
        return ( int ) Math.ceil( dim1.apply( right ) );
    }
    
    int minDim2_Floor() {
        return ( int ) Math.floor( dim2.apply( lowRight ) );
    }
    
    int maxDim2_Ceil() {
        return ( int ) Math.ceil( dim2.apply( highLeft ) );
    }
    
    @Override
    public String toString() {
        return "Plane{" +
            "maxD1=" + maxDim1_Ceil() +
            ", minD1=" + minDim1_Floor() +
            ", hl" + vec3ToStr( highLeft ) +
            ", r" + vec3ToStr( right ) +
            ", lr" + vec3ToStr( lowRight ) +
            ", l" + vec3ToStr( left ) +
        "}";
    }
    
}
