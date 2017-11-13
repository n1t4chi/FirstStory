/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

/**
 * @author n1t4chi
 */
public class AbsolutePlane2DVertices extends FramelessVertices2D {

    private static final AbsolutePlane2DVertices Plane2DVertices = new AbsolutePlane2DVertices();

    public static AbsolutePlane2DVertices getPlane2DVertices() {
        return Plane2DVertices;
    }

    private static float[] createPlane2DVerticesArray() {
        float[] pointData = {
        /*0*/ 0,0,
        /*1*/ 1,0,
        /*2*/ 1,1,
        /*3*/ 0,1
        };

        short[] points = {
            0,1,2,
            0,2,3
        };

        float[] rtrn = new float[points.length * 3];

        for ( int j = 0; j < points.length; j++ ) {
            rtrn[j * 3] = pointData[3 * points[j]];
            rtrn[j * 3 + 1] = pointData[3 * points[j] + 1];
            rtrn[j * 3 + 2] = pointData[3 * points[j] + 2];
        }
        return rtrn;
    }

    private AbsolutePlane2DVertices() {
        super( createPlane2DVerticesArray() );
    }

}
