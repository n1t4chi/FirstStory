/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;

import java.util.Collection;
import java.util.List;

/**
 * @author n1t4chi
 */
public class RenderableScene3DImpl implements RenderableScene3D {
    
    private final Camera3D camera;
    private final List< PositionableObject3D< ?, ? > > objects;
    private final Index3D terrainShift;
    private final Terrain3D< ? >[][][] terrains;
    
    public RenderableScene3DImpl(
        Camera3D camera,
        List< PositionableObject3D< ?, ? > > objects,
        Terrain3D< ? >[][][] terrains,
        Index3D terrainShift
    ) {
        this.camera = camera;
        this.objects = objects;
        this.terrainShift = terrainShift;
        this.terrains = terrains;
    }
    
    @Override
    public Terrain3D< ? >[][][] getTerrains3D() {
        return terrains;
    }
    
    @Override
    public Camera3D getScene3DCamera() {
        return camera;
    }
    
    @Override
    public Collection< PositionableObject3D< ?, ? > > getObjects3D() {
        return objects;
    }
    
    @Override
    public Index3D getTerrain3DShift() {
        return terrainShift;
    }
}
