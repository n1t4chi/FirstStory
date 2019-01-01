/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import org.joml.Vector3fc;

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
    
    Plane(
        Vector3fc highLeft,
        Vector3fc right,
        Vector3fc lowRight,
        Vector3fc left
    ) {
        this.highLeft = highLeft;
        this.right = right;
        this.lowRight = lowRight;
        this.left = left;
    }
    
    public Bounds getBoundsAtX( float x ) {
        if( x < minX_Floor() ) {
            return newBounds( 0, 0 );
        }
        if( x > maxX_Ceil() ) {
            return newBounds( 0, 0 );
        }
        
        var isRightToHightLeft = isToRight( x, highLeft.x() );
        var maxY = findY( x, highLeft, isRightToHightLeft ? right : left, isRightToHightLeft, true );
        
        var isRightToLowRight = isToRight( x, lowRight.x() );
        var minY = findY( x, lowRight, isRightToLowRight ? right : left, isRightToLowRight, false  );
        
        return newBounds( minY, maxY );
    }
    
    private float findY( float x, Vector3fc vec1, Vector3fc vec2, boolean isRight, boolean isUpper ) {
        var dx = vec1.x() - vec2.x();
        var shiftY = 0f;
        if( !isReallyClose( dx, 0 ) ) {
            var dy = vec1.y() - vec2.y();

            var shiftX = x - vec1.x();
            var mul = shiftX / dx;
            shiftY = dy * mul;
        }
        var y = vec1.y() + shiftY;
        
        if( isRight && x > vec2.x() || !isRight && x < vec2.x() ) {
            if ( isUpper && y < vec2.y() ) {
                y = vec2.y();
            } else if ( !isUpper && y > vec2.y() ) {
                y = vec2.y();
            }
        }
        
        return y;
    }
    
    private boolean isToRight( float x, float x2 ) {
        return x > x2;
    }
    
    private Bounds newBounds( float minY, float maxY ) {
        return new Bounds( ( int ) Math.floor( minY ), ( int ) Math.ceil( maxY ) );
    }
    
    int minX_Floor() {
        return ( int ) Math.floor( left.x() );
    }
    
    int maxX_Ceil() {
        return ( int ) Math.ceil( right.x() );
    }
    
    int minY_Floor() {
        return ( int ) Math.floor( lowRight.y() );
    }
    
    int maxY_Ceil() {
        return ( int ) Math.ceil( highLeft.y() );
    }
    
    @Override
    public String toString() {
        return "Plane{" +
            "maxX=" + maxX_Ceil() +
            ", minX=" + minX_Floor() +
            ", hl" + vec3ToStr( highLeft ) +
            ", r" + vec3ToStr( right ) +
            ", lr" + vec3ToStr( lowRight ) +
            ", l" + vec3ToStr( left ) +
        "}";
    }
    
}
