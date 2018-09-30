/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.object.data.Index2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;

import java.util.Collection;
import java.util.Collections;

/**
 * @author n1t4chi
 */
class EmptyRenderedObjects2D implements RenderableScene2D {
    
    public static final EmptyRenderedObjects2D instance = new EmptyRenderedObjects2D();
    
    public static EmptyRenderedObjects2D provide() {
        return instance;
    }
    
    private EmptyRenderedObjects2D() {}
    
    @Override
    public Terrain2D< ? >[][] getTerrains2D() {
        return new Terrain2D[ 0 ][];
    }
    
    @Override
    public Camera2D getScene2DCamera() {
        return IdentityCamera2D.getCamera();
    }
    
    @Override
    public Collection< PositionableObject2D< ?, ? > > getObjects2D() {
        return Collections.emptyList();
    }
    
    @Override
    public Index2D getTerrain2DShift() {
        return FirstOracleConstants.INDEX_ZERO_2I;
    }
}
