/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;

import java.util.Collection;
import java.util.List;

/**
 * @author n1t4chi
 */
public class RenderableScene2DImpl implements RenderableScene2D {
    
    private final Camera2D camera;
    private final List< PositionableObject2D< ?, ? > > objects;
    private final Index2D terrainShift;
    private final Terrain2D< ? >[][] terrains;
    
    public RenderableScene2DImpl(
        Camera2D camera,
        List< PositionableObject2D< ?, ? > > objects,
        Terrain2D< ? >[][] terrains,
        Index2D terrainShift
    ) {
        this.camera = camera;
        this.objects = objects;
        this.terrainShift = terrainShift;
        this.terrains = terrains;
    }
    
    @Override
    public Terrain2D< ? >[][] getTerrains2D() {
        return terrains;
    }
    
    @Override
    public Camera2D getScene2DCamera() {
        return camera;
    }
    
    @Override
    public Collection< PositionableObject2D< ?, ? > > getObjects2D() {
        return objects;
    }
    
    @Override
    public Index2D getTerrain2DShift() {
        return terrainShift;
    }
}
