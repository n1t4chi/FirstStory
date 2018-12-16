/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.Position2D;

import java.util.*;

import static com.firststory.firstoracle.FirstOracleConstants.SQRT3_DIV2;
import static com.firststory.firstoracle.data.Position2D.pos2;

/**
 * @author n1t4chi
 */
public class Hex2DVertices extends FramelessVertices2D {
    
    private static final Hex2DVertices Hex2DVertices = new Hex2DVertices();
    
    public static Hex2DVertices getHex2DVertices() {
        return Hex2DVertices;
    }
    
    private static List< Position2D > createHex2DVerticesArray() {
        Position2D[] pointData = {
        /*0*/ pos2( 0, 0 ),
        /*1*/ pos2( 1, 0 ),
        /*2*/ pos2( 0.5f, -SQRT3_DIV2 ),
        /*3*/ pos2( -0.5f, -SQRT3_DIV2 ),
        /*4*/ pos2( -1, 0 ),
        /*5*/ pos2( -0.5f, SQRT3_DIV2 ),
        /*6*/ pos2( 0.5f, SQRT3_DIV2 ),
        };

        short[] points = {
            0, 3, 2,
            0, 4, 3,
            0, 5, 4,
            0, 6, 5,
            0, 1, 6,
            0, 2, 1
        };
    
        List< Position2D > list = new ArrayList<>( points.length );
    
        for ( var point : points ) {
            list.add( pointData[ point ] );
        }
        return list;
    }
    
    private Hex2DVertices() {
        super( createHex2DVerticesArray() );
    }
    
}

