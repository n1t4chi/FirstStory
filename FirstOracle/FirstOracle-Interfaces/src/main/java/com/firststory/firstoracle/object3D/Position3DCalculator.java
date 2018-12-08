/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.PositionCalculator;

/**
 * @author n1t4chi
 */
public interface Position3DCalculator extends PositionCalculator< Index3D, Position3D > {
    
    default Position3D indexToPosition( int x, int y, int z, Index3D shift ) {
        return Position3D.pos3( 
            xIndexToPosition( x, shift ),
            yIndexToPosition( y, shift ),
            zIndexToPosition( z, shift )
        );
    }
    
    @Override
    default Position3D indexToPosition( Index3D index, Index3D shift ) {
        return indexToPosition( index.x(), index.y(), index.z(), shift );
    }
    
    default Index3D positionToIndex( float x, float y, float z, Index3D shift ) {
        return Index3D.id3(
            xPositionToIndex( x, shift ),
            yPositionToIndex( y, shift ),
            zPositionToIndex( z, shift )
        );
    }
    
    default Index3D positionToIndex( Position3D position, Index3D shift ) {
        return positionToIndex( position.x(), position.y(), position.z(), shift );
    }
    
    float xIndexToPosition( int x, Index3D shift );
    
    float yIndexToPosition( int y, Index3D shift );
    
    float zIndexToPosition( int z, Index3D shift );
    
    int xPositionToIndex( float x, Index3D shift );
    
    int yPositionToIndex( float y, Index3D shift );
    
    int zPositionToIndex( float z, Index3D shift );
}
