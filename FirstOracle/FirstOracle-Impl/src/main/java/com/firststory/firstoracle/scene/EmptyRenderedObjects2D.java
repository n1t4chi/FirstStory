/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;
import org.joml.Vector2ic;

import java.util.Collection;
import java.util.Collections;

/**
 * @author n1t4chi
 */
class EmptyRenderedObjects2D implements RenderedScene2D {
    
    @Override
    public Terrain2D< ? >[][] getTerrains() {
        return new Terrain2D[ 0 ][];
    }
    
    @Override
    public Collection< PositionableObject2D< ?, ? > > getObjects() {
        return Collections.emptyList();
    }
    
    @Override
    public Vector2ic getTerrainShift() {
        return FirstOracleConstants.VECTOR_ZERO_2I;
    }
}
