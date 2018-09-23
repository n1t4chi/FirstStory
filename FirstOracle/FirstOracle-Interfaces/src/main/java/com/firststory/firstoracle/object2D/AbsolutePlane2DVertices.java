/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.Vertex2D;

import java.util.ArrayList;
import java.util.List;

import static com.firststory.firstoracle.object.Vertex2D.vec2;

/**
 * @author n1t4chi
 */
public class AbsolutePlane2DVertices extends FramelessVertices2D {
    
    private static final AbsolutePlane2DVertices Plane2DVertices = new AbsolutePlane2DVertices();
    
    public static AbsolutePlane2DVertices getPlane2DVertices() {
        return Plane2DVertices;
    }
    
    private static List< Vertex2D > createPlane2DVerticesArray() {
        Vertex2D[] pointData = {
        /*0*/ vec2( 0, 0 ),
        /*1*/ vec2( 1, 0 ),
        /*2*/ vec2( 1, 1 ),
        /*3*/ vec2( 0, 1 )
        };

        short[] points = {
            0, 1, 2,
            0, 2, 3
        };
    
        List< Vertex2D > list = new ArrayList<>( points.length );
    
        for ( short point : points ) {
            list.add( pointData[ point ] );
        }
        return list;
    }
    
    private AbsolutePlane2DVertices() {
        super( createPlane2DVerticesArray() );
    }
    
}
