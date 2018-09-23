/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.Vertex2D;

import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.singletonArray;

/**
 * @author n1t4chi
 */
public class FramelessVertices2D extends Vertices2D {
    
    public FramelessVertices2D( List< Vertex2D > vertices ) {
        super( singletonArray( vertices ) );
    }
}