/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

/**
 * @author n1t4chi
 */
public class AbsolutePlane3DVertices extends FramelessVertices3D {

    private static AbsolutePlane3DVertices Plane3DVertices = new AbsolutePlane3DVertices();

    public static AbsolutePlane3DVertices getPlane3DVertices() {
        return Plane3DVertices;
    }

    private static float[] createPlane3DVerticesArray() {
        float[] pointData = {
        /*0*/ -1,-1,0,
        /*1*/ 1,-1,0,
        /*2*/ 1,1,0,
        /*3*/ -1,1,0

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

    private AbsolutePlane3DVertices() {
        super( createPlane3DVerticesArray() );
    }

}
