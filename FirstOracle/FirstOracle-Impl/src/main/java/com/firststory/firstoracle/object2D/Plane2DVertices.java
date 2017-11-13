/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

/**
 * @author n1t4chi
 */
public class Plane2DVertices extends FramelessVertices2D {

    private static Plane2DVertices Plane2DVertices = new Plane2DVertices();

    public static Plane2DVertices getPlane2DVertices() {
        return Plane2DVertices;
    }

    private static float[] createPlane2DVerticesArray() {
        float[] pointData = {
        /*0*/ -1,-1,
        /*1*/ 1,-1,
        /*2*/ 1,1,
        /*3*/ -1,1
        };

        short[] points = {
            0,1,2,
            0,2,3
        };

        float[] rtrn = new float[points.length * 2];

        for ( int j = 0; j < points.length; j++ ) {
            rtrn[j * 2] = pointData[2 * points[j]];
            rtrn[j * 2 + 1] = pointData[2 * points[j] + 1];
        }
        return rtrn;
    }

    private Plane2DVertices() {
        super( createPlane2DVerticesArray() );
    }

}
