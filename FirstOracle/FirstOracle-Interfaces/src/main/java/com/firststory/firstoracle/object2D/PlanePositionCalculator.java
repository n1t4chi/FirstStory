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
public class PlanePositionCalculator implements Position2DCalculator {
    
    public static Position2D computePlanePosition( int x, int y, Index2D shift ) {
        return Position2D.pos2(
            FirstOracleConstants.transPlaneDiscreteToSpace( x, shift.x() ),
            FirstOracleConstants.transPlaneDiscreteToSpace( y, shift.y() )
        );
    }
    
    @Override
    public Position2D compute( int x, int y, Index2D shift ) {
        return computePlanePosition( x, y, shift );
    }
}
