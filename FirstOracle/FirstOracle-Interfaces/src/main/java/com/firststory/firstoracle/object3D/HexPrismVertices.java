/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.Vertex3D;

import java.util.ArrayList;
import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.SQRT3_DIV2;
import static com.firststory.firstoracle.object.Vertex3D.vec3;

/**
 * @author n1t4chi
 */
public class HexPrismVertices extends FramelessVertices3D {

    private static final HexPrismVertices cubeVertices = new HexPrismVertices();

    public static HexPrismVertices getHexPrismVertices() {
        return cubeVertices;
    }

    private static List< Vertex3D > createHexPrismVerticesArray() {
        Vertex3D[] pointData = {
        /*0*/ vec3( 0, -1, 0 ),

        /*1*/ vec3( 1, -1, 0 ),
        /*2*/ vec3( 0.5f, -1, -SQRT3_DIV2 ),
        /*3*/ vec3( -0.5f, -1, -SQRT3_DIV2 ),
        /*4*/ vec3( -1, -1, 0 ),
        /*5*/ vec3( -0.5f, -1, SQRT3_DIV2 ),
        /*6*/ vec3( 0.5f, -1, SQRT3_DIV2 ),

        /*7*/ vec3( 0, 1, 0 ),

        /*8*/ vec3( 1, 1, 0 ),
        /*9*/ vec3( 0.5f, 1, -SQRT3_DIV2 ),
        /*10*/ vec3( -0.5f, 1, -SQRT3_DIV2 ),
        /*11*/ vec3( -1, 1, 0 ),
        /*12*/ vec3( -0.5f, 1, SQRT3_DIV2 ),
        /*13*/ vec3( 0.5f, 1, SQRT3_DIV2 ),
        };

        short[] points = {
            //face 0
            2, 3, 10,
            2, 10, 9,
            //face 1
            3, 4, 11,
            3, 11, 10,
            //face 2
            4, 5, 12,
            4, 12, 11,
            //face 3
            5, 6, 13,
            5, 13, 12,
            //face 4
            6, 1, 8,
            6, 8, 13,
            //face 5
            1, 2, 9,
            1, 9, 8,
    
            //face 6
            7, 9, 10,
            7, 10, 11,
            7, 11, 12,
            7, 12, 13,
            7, 13, 8,
            7, 8, 9,

            //face 7
            0, 2, 3,
            0, 3, 4,
            0, 4, 5,
            0, 5, 6,
            0, 6, 1,
            0, 1, 2
        };
    
        List< Vertex3D > list = new ArrayList<>( points.length );
    
        for ( short point : points ) {
            list.add( pointData[ point ] );
        }
        return list;
    }

    private HexPrismVertices() {
        super( createHexPrismVerticesArray() );
    }

}
