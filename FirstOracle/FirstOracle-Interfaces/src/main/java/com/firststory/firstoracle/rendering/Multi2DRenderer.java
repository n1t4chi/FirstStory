/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.object2D.Object2D;
import com.firststory.firstoracle.object2D.Terrain2D;

/**
 * @author n1t4chi
 */
public interface Multi2DRenderer extends PositionableObject2DRenderer, Terrain2DRenderer {
    
    @Override
    default void render( GraphicObject object ) {
        if ( object instanceof Terrain2D ) {
            renderTerrain( ( Terrain2D ) object,
                FirstOracleConstants.VECTOR_ZERO_2F,
                FirstOracleConstants.VECTOR_ZERO_4F,
                1f
            );
        } else if ( object instanceof Object2D ) {
            renderObject( ( Object2D ) object, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
        }
    }
    
    @Override
    default void render( Object2D object ) {
        renderObject( object, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
    
    @Override
    default void render( Terrain2D object ) {
        renderTerrain( object, FirstOracleConstants.VECTOR_ZERO_2F, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
}
