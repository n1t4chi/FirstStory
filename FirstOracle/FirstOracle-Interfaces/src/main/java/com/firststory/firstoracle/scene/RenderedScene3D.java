/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.object3D.Terrain3D;
import org.joml.Vector3ic;

/**
 * @author n1t4chi
 */
public interface RenderedScene3D extends RenderedObjects3D {
    
    Terrain3D[][][] getTerrains();
    Vector3ic getTerrainShift();
}
