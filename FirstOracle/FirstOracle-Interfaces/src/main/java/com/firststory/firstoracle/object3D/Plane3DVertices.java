/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.data.Position3D;

import java.util.ArrayList;
import java.util.List;

import static com.firststory.firstoracle.object.data.Position3D.pos3;

/**
 * @author n1t4chi
 */
public class Plane3DVertices extends FramelessVertices3D {

    private static final Plane3DVertices Plane3DVertices = new Plane3DVertices();

    public static Plane3DVertices getPlane3DVertices() {
        return Plane3DVertices;
    }

    private static List< Position3D > createPlane3DVerticesArray() {
        Position3D[] pointData = {
        /*0*/ pos3( -1, -1, 0 ),
        /*1*/ pos3( 1, -1, 0 ),
        /*2*/ pos3( 1, 1, 0 ),
        /*3*/ pos3( -1, 1, 0)
        };

        short[] points = {
            0, 1, 2,
            0, 2, 3
        };
    
        List< Position3D > list = new ArrayList<>( points.length );
    
        for ( var point : points ) {
            list.add( pointData[ point ] );
        }
        return list;
    }

    private Plane3DVertices() {
        super( createPlane3DVerticesArray() );
    }

}
