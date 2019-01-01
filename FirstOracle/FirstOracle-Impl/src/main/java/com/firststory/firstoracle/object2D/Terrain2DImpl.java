/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.UvMap;

/**
 * @author n1t4chi
 */
public class Terrain2DImpl
    extends AbstractTerrain2D< Vertices2D, Position2DCalculator >
{
    private UvMap uvMap;
    private Vertices2D vertices;
    private Position2DCalculator calculator;
    
    public void setUvMap( UvMap uvMap ) {
        this.uvMap = uvMap;
    }
    
    public void setVertices( Vertices2D vertices ) {
        this.vertices = vertices;
    }
    
    public void setPositionCalculator( Position2DCalculator calculator ) { this.calculator = calculator; }
    
    @Override
    public Position2DCalculator getPositionCalculator() {
        return calculator;
    }
    
    @Override
    public UvMap getUvMap() {
        return uvMap;
    }
    
    @Override
    public Vertices2D getVertices() {
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
