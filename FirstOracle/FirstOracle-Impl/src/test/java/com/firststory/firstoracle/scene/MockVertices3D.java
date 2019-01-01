/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.object3D.Vertices3D;

import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.singletonArray;
import static com.firststory.firstoracle.data.Position3D.pos3;

/**
 * @author n1t4chi
 */
class MockVertices3D extends Vertices3D {
    
    public MockVertices3D() {
        super( singletonArray( List.of(
            pos3( 1, 1, 1 ),
            pos3( 0, 1, 1 ),
            pos3( 1, 0, 1 ),
            pos3( 1, 1, 0 ),
            pos3( 0, 0, 1 ),
            pos3( 1, 0, 0 ),
            pos3( 0, 1, 0 ),
            pos3( 0, 0, 0 )
        ) ) );
    }
}
