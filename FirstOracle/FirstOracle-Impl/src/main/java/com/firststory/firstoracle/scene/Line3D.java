/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.FirstOracleConstants;
import org.joml.*;

import static com.firststory.firstoracle.FirstOracleConstants.vec3ToStr;

/**
 * @author n1t4chi
 */
class Line3D {
    
    private final Vector3fc point0;
    private final Vector3fc point1;
    private final Vector3fc direction;
    
    Line3D( Vector3fc point0, Vector3fc point1 ) {
        this.point0 = point0;
        this.point1 = point1;
        this.direction = calcDirection( point0, point1 );
    }
    
    Vector3fc getPoint0() {
        return point0;
    }
    
    Vector3fc getPoint1() {
        return point1;
    }
    
    Vector3fc getDirection() {
        return direction;
    }
    
    Vector3fc getPointAtX( float x ) {
        return getPointAt( x, point0.x(), direction.x() );
    }
    
    Vector3fc getPointAtY( float y ) {
        return getPointAt( y, point0.y(), direction.y() );
    }
    
    Vector3fc getPointAtZ( float z ) {
        return getPointAt( z, point0.z(), direction.z() );
    }
    
    boolean sameX( Line3D line ) {
        return FirstOracleConstants.isReallyClose( point0.x(), line.point0.x() );
    }
    
    boolean sameY( Line3D line ) {
        return FirstOracleConstants.isReallyClose( point0.y(), line.point0.y() );
    }
    
    boolean sameZ( Line3D line ) {
        return FirstOracleConstants.isReallyClose( point0.z(), line.point0.z() );
    }
    
    /**
     * Returns whether this line is to the right on X ( bigger ) than the other
     * @param line other line
     * @return true if this line's point0 is to the right on X than the other line
     */
    boolean toRightOf( Line3D line ) {
        return point0.x() > line.point0.x();
    }
    
    /**
     * Returns whether this line is above ( bigger ) than the other
     * @param line other line
     * @return true if this line's point0 is above on Y than the other line
     */
    boolean above( Line3D line ) {
        return point0.x() == line.point0.x();
    }
    
    /**
     * Returns whether this line is deeper ( bigger ) on Z than the other
     * @param line other line
     * @return true if this line's point0 has deeper Z coordinate than the other
     */
    boolean deeperThan( Line3D line ) {
        return point0.z() > line.point0.y();
    }
    
    private Vector3fc getPointAt( float dim, float point0Dim, float directionDim ) {
        if( FirstOracleConstants.isReallyClose( directionDim, 0 ) ) {
            if( FirstOracleConstants.isClose( point0Dim, dim ) ) {
                return point0;
            }
            return null;
        }
        var t = ( dim - point0Dim ) / directionDim;
        return new Vector3f( this.direction ).mul( t ).add( point0 );
    }
    
    private Vector3fc calcDirection( Vector3fc point0, Vector3fc point1 ) {
        var direction = new Vector3f();
        point1.sub( point0, direction );
        direction.normalize();
        return direction;
    }
    
    @Override
    public String toString() {
        return "Line3D{" +
               "p0" + vec3ToStr( point0 ) +
               ", p1" + vec3ToStr( point1 ) +
               ", dir" + vec3ToStr( direction ) +
           '}';
    }
}
