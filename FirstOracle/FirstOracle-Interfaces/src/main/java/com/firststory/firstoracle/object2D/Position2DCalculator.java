/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Position2D;

/**
 * @author n1t4chi
 */
public interface Position2DCalculator {
    Position2D compute( int x, int y, Index2D shift );
}
