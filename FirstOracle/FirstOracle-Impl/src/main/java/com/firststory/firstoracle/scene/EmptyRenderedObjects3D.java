/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;
import org.joml.Vector3ic;

import java.util.Collection;
import java.util.Collections;

/**
 * @author n1t4chi
 */
class EmptyRenderedObjects3D implements RenderedScene3D {
    
    @Override
    public Terrain3D< ? >[][][] getTerrains() {
        return new Terrain3D[ 0 ][][];
    }
    
    @Override
    public Collection< PositionableObject3D< ?, ? > > getObjects() {
        return Collections.emptyList();
    }
    
    @Override
    public Vector3ic getTerrainShift() {
        return FirstOracleConstants.VECTOR_ZERO_3I;
    }
}
