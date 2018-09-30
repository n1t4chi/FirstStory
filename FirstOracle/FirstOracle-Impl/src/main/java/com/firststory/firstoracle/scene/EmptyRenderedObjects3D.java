/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.camera3D.IdentityCamera3D;
import com.firststory.firstoracle.object.data.Index3D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;

import java.util.Collection;
import java.util.Collections;

/**
 * @author n1t4chi
 */
class EmptyRenderedObjects3D implements RenderableScene3D {
    
    public static final EmptyRenderedObjects3D instance = new EmptyRenderedObjects3D();
    
    public static EmptyRenderedObjects3D provide() {
        return instance;
    }
    
    private EmptyRenderedObjects3D() {}
    
    @Override
    public Terrain3D< ? >[][][] getTerrains3D() {
        return new Terrain3D[ 0 ][][];
    }
    
    @Override
    public Collection< PositionableObject3D< ?, ? > > getObjects3D() {
        return Collections.emptyList();
    }
    
    @Override
    public Index3D getTerrain3DShift() {
        return FirstOracleConstants.INDEX_ZERO_3I;
    }
    
    @Override
    public Camera3D getScene3DCamera() {
        return IdentityCamera3D.getCamera();
    }
}
