/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

/**
 * @author n1t4chi
 */
public class CubeVertices extends FramelessVertices3D {

    private static CubeVertices cubeVertices = new CubeVertices();

    public static CubeVertices getCubeVertices() {
        return cubeVertices;
    }

    private static float[] createCubeVerticesArray() {
        float[] pointData = {
        /*0*/ -1,-1,-1,
        /*1*/ 1,-1,-1,
        /*2*/ 1,1,-1,
        /*3*/ -1,1,-1,
        /*4*/ -1,-1,1,
        /*5*/ 1,-1,1,
        /*6*/ 1,1,1,
        /*7*/ -1,1,1
        };

        short[] points = {
            //face 0
            1,0,3,
            1,3,2,

            //face 1
            0,4,7,
            0,7,3,

            //face 2
            4,5,6,
            4,6,7,

            //face 3
            5,1,2,
            5,2,6,

            //face 4
            7,6,2,
            7,2,3,

            //face 5
            4,5,1,
            4,1,0
        };

        float[] rtrn = new float[points.length * 3];

        for ( int j = 0; j < points.length; j++ ) {
            rtrn[j * 3] = pointData[3 * points[j]];
            rtrn[j * 3 + 1] = pointData[3 * points[j] + 1];
            rtrn[j * 3 + 2] = pointData[3 * points[j] + 2];
        }
        return rtrn;
    }

    private CubeVertices() {
        super( createCubeVerticesArray() );
    }

}
