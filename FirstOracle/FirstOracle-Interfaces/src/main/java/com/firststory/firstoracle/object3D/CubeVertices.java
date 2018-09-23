/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.Vertex3D;

import java.util.ArrayList;
import java.util.List;

import static com.firststory.firstoracle.object.Vertex3D.vec3;

/**
 * @author n1t4chi
 */
public class CubeVertices extends FramelessVertices3D {

    private static final CubeVertices cubeVertices = new CubeVertices();

    public static CubeVertices getCubeVertices() {
        return cubeVertices;
    }

    private static List< Vertex3D > createCubeVerticesArray() {
        Vertex3D[] pointData = {
        /*0*/ vec3( -1, -1, -1 ),
        /*1*/ vec3(1, -1, -1 ),
        /*2*/ vec3(1, 1, -1 ),
        /*3*/ vec3(-1, 1, -1 ),
        /*4*/ vec3(-1, -1, 1 ),
        /*5*/ vec3(1, -1, 1 ),
        /*6*/ vec3(1, 1, 1 ),
        /*7*/ vec3(-1, 1, 1 )
        };

        short[] points = {
            //face 0
            1, 0, 3,
            1, 3, 2,

            //face 1
            0, 4, 7,
            0, 7, 3,

            //face 2
            4, 5, 6,
            4, 6, 7,

            //face 3
            5, 1, 2,
            5, 2, 6,

            //face 4
            7, 6, 2,
            7, 2, 3,

            //face 5
            4, 5, 1,
            4, 1, 0
        };
    
        List< Vertex3D > list = new ArrayList<>( points.length );
    
        for ( short point : points ) {
            list.add( pointData[ point ] );
        }
        return list;
    }

    private CubeVertices() {
        super( createCubeVerticesArray() );
    }

}
