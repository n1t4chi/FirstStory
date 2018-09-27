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
public class AbsolutePlane3DVertices extends FramelessVertices3D {

    private static final AbsolutePlane3DVertices Plane3DVertices = new AbsolutePlane3DVertices();

    public static AbsolutePlane3DVertices getPlane3DVertices() {
        return Plane3DVertices;
    }

    private static List< Vertex3D > createPlane3DVerticesArray() {
        Vertex3D[] pointData = {
        /*0*/ vec3( -1, -1, 0 ),
        /*1*/ vec3( 1, -1, 0 ),
        /*2*/ vec3( 1, 1, 0 ),
        /*3*/ vec3( -1, 1, 0 )

        };

        short[] points = {
            0, 1, 2,
            0, 2, 3
        };
    
        List< Vertex3D > list = new ArrayList<>( points.length );
    
        for ( var point : points ) {
            list.add( pointData[ point ] );
        }
        return list;
    }

    private AbsolutePlane3DVertices() {
        super( createPlane3DVerticesArray() );
    }

}
