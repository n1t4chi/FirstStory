/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.object3D.Object3D;
import com.firststory.firstoracle.object3D.Terrain3D;

/**
 * @author n1t4chi
 */
public interface Multi3DRenderer extends PositionableObject3DRenderer, Terrain3DRenderer {
    
    @Override
    default void render( GraphicObject object ) {
        if ( object instanceof Terrain3D ) {
            renderTerrain( ( Terrain3D ) object,
                FirstOracleConstants.VECTOR_ZERO_3F,
                FirstOracleConstants.VECTOR_ZERO_4F,
                1f
            );
        } else if ( object instanceof Object3D ) {
            renderObject( ( Object3D ) object, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
        }
    }
    
    @Override
    default void render( Object3D object ) {
        renderObject( object, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
    
    @Override
    default void render( Terrain3D object ) {
        renderTerrain( object, FirstOracleConstants.VECTOR_ZERO_3F, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
}
