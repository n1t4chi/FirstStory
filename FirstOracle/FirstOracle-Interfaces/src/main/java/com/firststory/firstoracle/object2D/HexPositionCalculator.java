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
public class HexPositionCalculator implements Position2DCalculator {
    
    public static Position2D computeHexPosition( int x, int y, Index2D shift ) {
        return Position2D.pos2(
            FirstOracleConstants.transHexXDiscreteToSpace( x, shift.x() ),
            FirstOracleConstants.transHexYDiscreteToSpace( x, y, shift.x(), shift.y() )
        );
    }
    
    @Override
    public Position2D compute( int x, int y, Index2D shift ) {
        return computeHexPosition( x, y, shift );
    }
}
