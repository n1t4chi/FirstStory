/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera3D;

import org.joml.Vector4f;

/**
 * @author n1t4chi
 */
public class CameraBoxXZDimension {
    
    /*     
             p1-----p2
             |       |
             |   +---+---->x
             |   |   |
             p4--+--p3
                 |  
                \|/z
    
        p1 is lowest on X, if 2 points have same X then lowest on Z is taken
        p2 is connected with p2 [like p4] and is further on X than p4, or lower on Z if both have same X
     */
    //private final Vector4f[] points;
    /**
     * lines[x] connects points[x] and point[(x+1)%4]
     */
    private final LineData[] lines = new LineData[ 4 ];
    
    private final float minX;
    private final float maxX;
    
    public CameraBoxXZDimension( Vector4f[] points ) {
        //this.points = points;
        this.minX = java.lang.Math.min( java.lang.Math.min( points[ 0 ].x, points[ 1 ].x ),
            java.lang.Math.min( points[ 2 ].x, points[ 3 ].x ) );
        this.maxX = java.lang.Math.max( java.lang.Math.max( points[ 0 ].x, points[ 1 ].x ),
            java.lang.Math.max( points[ 2 ].x, points[ 3 ].x ) );
        
        for ( int j = 0; j < 4; j++ ) {
            lines[ j ] = new LineData( points[ j ], points[ ( j + 1 ) % 4 ] );
        }
    }
    
    public float getMaxX() {
        return maxX;
    }
    
    public float getMinX() {
        return minX;
    }
    
    /**
     * Returns minimum and maximum Z values for given X.<br>
     * If no such values were found then pair min/max = (1,-1) is returned which should fail every time on min&lt;=z {@literal @}{@literal @} z&lt;=max check
     *
     * @param X x position
     *
     * @return min/max Z
     */
    public CameraBoxZDimension getZDimension( float X ) {
        float z1 = 1, z2 = -1;
        if ( minX <= X && X <= maxX ) {
            float[] Zs = new float[ 2 ];
            int Zs_it = 0;
            for ( LineData ld : lines ) {
                float[] lineZs = ld.getZsOnGivenX( X );
                for ( int i = 0; i < lineZs.length && Zs_it < 2; i++ ) {
                    boolean is = true;
                    for ( int j = 0; j < Zs_it; j++ ) {
                        if ( lineZs[ i ] == Zs[ j ] ) {
                            is = false;
                            break;
                        }
                    }
                    if ( is ) {
                        Zs[ Zs_it ] = lineZs[ i ];
                        Zs_it++;
                    }
                }
            }
            if ( Zs_it > 0 ) {
                if ( Zs_it >= 2 ) {
                    if ( Zs[ 0 ] <= Zs[ 1 ] ) {
                        z1 = Zs[ 0 ];
                        z2 = Zs[ 1 ];
                    } else {
                        z1 = Zs[ 1 ];
                        z2 = Zs[ 0 ];
                    }
                } else {
                    z1 = z2 = Zs[ 0 ];
                }
            }
        }
        
        return new CameraBoxZDimension( z1, z2 );
    }
    
    private boolean withinRectangle(
        float X,
        float Z,
        Vector4f rectanglePoint1,
        Vector4f rectanglePoint2
    ) {
        return
            (
                ( rectanglePoint1.x <= X && X <= rectanglePoint2.x ) ||
                    ( rectanglePoint2.x <= X && X <= rectanglePoint1.x )
            )
                &&
                (
                    ( rectanglePoint1.z <= Z && Z <= rectanglePoint2.z ) ||
                        ( rectanglePoint2.z <= Z && Z <= rectanglePoint1.z )
                )
            ;
    }
    
    private class LineData {
        
        private final float intercept, gradient;
        private final Vector4f p1, p2;
    
        private LineData( Vector4f point1, Vector4f point2 ) {
            p1 = point1;
            p2 = point2;
            gradient = ( point2.z - point1.z ) / ( point2.x - point1.x );
            intercept = point1.z - gradient * point1.x;
        }
        
        @Override
        public String toString() {
            return p1 + "" + p2 + " z=" + gradient + "*x+" + intercept;
        }
        
        private float[] getZsOnGivenX( float X ) {
            float[] rtrn;

//            int branch = 0;
            if ( Float.isFinite( gradient ) ) {
                if ( gradient == 0 ) {
                    float d1 = p1.x - X;
                    float d2 = p2.x - X;
                    if ( ( d1 <= 0.01 && d1 >= -0.01 ) || ( d2 <= 0.01 && d2 >= -0.01 ) ) {
                        rtrn = new float[ 2 ];
                        rtrn[ 0 ] = p1.z;
                        rtrn[ 1 ] = p2.z;
//                        branch = 1;
                    } else {
                        rtrn = new float[ 0 ];
//                        branch = 2;
                    }
                } else {
                    float z = gradient * X + intercept;
                    if ( withinRectangle( X, z, p1, p2 ) ) {
                        rtrn = new float[ 1 ];
                        rtrn[ 0 ] = z;
//                        branch = 3;
                    } else {
                        rtrn = new float[ 0 ];
//                        branch = 4;
                    }
                }
                // infinity/nan -> parallel to Z
            } else {
                if ( ( p1.x <= X && X <= p2.x ) || ( p2.x <= X && X <= p1.x ) ) {
                    rtrn = new float[ 1 ];
                    rtrn[ 0 ] = p1.z;
//                    branch = 5;
                } else {
                    rtrn = new float[ 0 ];
//                    branch = 6;
                }
            }
            //System.err.println(toString()+" X:"+X+" z:"+(gradient*X+intercept)+" rtrn:"+Arrays.toString(rtrn)+" branch:"+branch);
            return rtrn;
        }
        
    }
    
}
