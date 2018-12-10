/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import org.joml.Vector3fc;

import java.util.function.Function;

import static com.firststory.firstoracle.FirstOracleConstants.*;

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
            return null;
        }
        if( d1 > maxDim1_Ceil() ) {
            return null;
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
    
    private boolean isToRight( float d1_1, float d1_2 ) {
        return d1_1 > d1_2;
    }
    
    private Bounds newBounds( float d2_1, float d2_2 ) {
        var minD2 = Math.min( d2_1,d2_2 );
        var maxD2 = Math.max( d2_1,d2_2 );
        return new Bounds( ( int ) Math.floor( minD2 ), ( int ) Math.ceil( maxD2 ) );
    }
    
    int minDim1_Floor() {
        return ( int ) Math.floor( Math.min( dim1.apply( left ), dim1.apply( right ) ) );
    }
    
    int maxDim1_Ceil() {
        return ( int ) Math.ceil( Math.max( dim1.apply( left ), dim1.apply( right ) ) );
    }
    
    int minDim2_Floor() {
        return ( int ) Math.floor( Math.min( dim2.apply( lowRight ), dim2.apply( highLeft ) ) );
    }
    
    int maxDim2_Ceil() {
        return ( int ) Math.ceil( Math.max( dim2.apply( lowRight ), dim2.apply( highLeft ) ) );
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
