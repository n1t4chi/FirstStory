/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.UvMap;

/**
 * @author n1t4chi
 */
public class Terrain3DImpl
    extends AbstractTerrain3D< Vertices3D, Position3DCalculator >
{
    private UvMap uvMap;
    private Vertices3D vertices;
    private Position3DCalculator calculator;
    
    public void setUvMap( UvMap uvMap ) {
        this.uvMap = uvMap;
    }
    
    public void setVertices( Vertices3D vertices ) {
        this.vertices = vertices;
    }
    
    public void setPositionCalculator( Position3DCalculator calculator ) { this.calculator = calculator; }
    
    @Override
    public Position3DCalculator getPositionCalculator() {
        return calculator;
    }
    
    @Override
    public UvMap getUvMap() {
        return uvMap;
    }
    
    @Override
    public Vertices3D getVertices() {
        return vertices;
    }
    
    @Override
    public int getCurrentUvMapDirection( double currentCameraRotation ) {
        return 0;
    }
    
    @Override
    public int getCurrentUvMapFrame( double currentTimeSnapshot ) {
        return 0;
    }
    
    @Override
    public int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
}
