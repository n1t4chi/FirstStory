/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.object.Colouring;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;

/**
 * @author n1t4chi
 */
public class Terrain2DImpl
    extends AbstractTerrain2D< Vertices2D >
{
    private UvMap uvMap;
    private Texture texture;
    private Vertices2D vertices;
    private Colouring colouring;
    private Position2DCalculator calculator;
    
    public void setColouring( Colouring colouring ) {
        this.colouring = colouring;
    }
    
    public void setUvMap( UvMap uvMap ) {
        this.uvMap = uvMap;
    }
    
    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
    
    public void setVertices( Vertices2D vertices ) {
        this.vertices = vertices;
    }
    
    public void setPositionCalculator( Position2DCalculator calculator ) { this.calculator = calculator; }
    
    @Override
    public Colouring getColouring() {
        return colouring;
    }
    
    @Override
    public Texture getTexture() {
        return texture;
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
    
    @Override
    public Position2D computePosition(
        int x,
        int y,
        Index2D arrayShift
    ) {
        return calculator.compute( x, y, arrayShift );
    }
}
