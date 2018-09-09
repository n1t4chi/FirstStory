/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.object2D.Terrain2D;
import org.joml.Vector2ic;

/**
 * @author n1t4chi
 */
public interface RenderedScene2D extends RenderedObjects2D {
    
    Terrain2D[][] getTerrains();
    Vector2ic getTerrainShift();
}
