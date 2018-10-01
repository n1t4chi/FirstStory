/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Position2D;

/**
 * @author n1t4chi
 */
public interface RectangleGrid
    extends Terrain2D< Plane2DVertices >, Rectangle< Identity2DTransformations >
{
    
    @Override
    default Position2D computePosition( int x, int y, Index2D arrayShift ) {
        return Position2D.pos2(
            FirstOracleConstants.transPlaneDiscreteToSpace( x, arrayShift.x() ),
            FirstOracleConstants.transPlaneDiscreteToSpace( y, arrayShift.y() )
        );
    }
}
