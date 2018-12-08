/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.PositionCalculator;

/**
 * @author n1t4chi
 */
public interface Position2DCalculator extends PositionCalculator< Index2D, Position2D > {
    
    default Position2D indexToPosition( int x, int y, Index2D shift ) {
        return Position2D.pos2(
            xIndexToPosition( x, shift ),
            yIndexToPosition( y, shift )
        );
    }
    
    @Override
    default Position2D indexToPosition( Index2D index, Index2D shift ) {
        return indexToPosition( index.x(), index.y(), shift );
    }
    
    default Index2D positionToIndex( float x, float y, Index2D shift ) {
        return Index2D.id2(
            xPositionToIndex( x, shift ),
            yPositionToIndex( y, shift )
        );
    }
    
    default Index2D positionToIndex( Position2D position, Index2D shift ) {
        return positionToIndex( position.x(), position.y(), shift );
    }
    
    float xIndexToPosition( int x, Index2D shift );
    
    float yIndexToPosition( int y, Index2D shift );
    
    int xPositionToIndex( float x, Index2D shift );
    
    int yPositionToIndex( float y, Index2D shift );
}
