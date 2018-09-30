/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.data.Position3D;

import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.singletonArray;

/**
 * @author n1t4chi
 */
public class FramelessVertices3D extends Vertices3D {
    
    public FramelessVertices3D( List< Position3D > vertices ) {
        super( singletonArray( vertices ) );
    }
}
