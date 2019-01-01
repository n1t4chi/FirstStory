/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.object2D.Vertices2D;

import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.singletonArray;
import static com.firststory.firstoracle.data.Position2D.pos2;

/**
 * @author n1t4chi
 */
class MockVertices2D extends Vertices2D {
    
    public MockVertices2D() {
        super( singletonArray( List.of(
            pos2( 0, 0 ),
            pos2( 1, 0 ),
            pos2( 1, 1 ),
            pos2( 0, 1 )
        ) ) );
    }
}
