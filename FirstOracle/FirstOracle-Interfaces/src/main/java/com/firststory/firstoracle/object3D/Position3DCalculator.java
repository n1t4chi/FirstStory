/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.data.Position3D;

/**
 * @author n1t4chi
 */
public interface Position3DCalculator {
    Position3D compute(
        int x,
        int y,
        int z,
        Index3D shift
    );
}
