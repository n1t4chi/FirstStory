/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import static com.firststory.firstoracle.FirstOracleConstants.SQRT3_DIV2;

/**
 * @author n1t4chi
 */
public class Hex2DVertices extends FramelessVertices2D {
    
    private static final Hex2DVertices Hex2DVertices = new Hex2DVertices();
    
    public static Hex2DVertices getHex2DVertices() {
        return Hex2DVertices;
    }
    
    private static float[] createHex2DVerticesArray() {
        float[] pointData = {
        /*0*/ 0, 0,

        /*1*/ 1, 0,
        /*2*/ 0.5f, -SQRT3_DIV2,
        /*3*/ -0.5f, -SQRT3_DIV2,
        /*4*/ -1, 0,
        /*5*/ -0.5f, SQRT3_DIV2,
        /*6*/ 0.5f, SQRT3_DIV2,
        };

        short[] points = {
            0, 2, 3,
            0, 3, 4,
            0, 4, 5,
            0, 5, 6,
            0, 6, 1,
            0, 1, 2
        };
        
        float[] rtrn = new float[ points.length * 2 ];

        for ( int j = 0; j < points.length; j++ ) {
            rtrn[ j * 2 ] = pointData[ 2 * points[ j ] ];
            rtrn[ j * 2 + 1 ] = pointData[ 2 * points[ j ] + 1 ];
        }
        return rtrn;
    }
    
    private Hex2DVertices() {
        super( createHex2DVerticesArray() );
    }
    
}

