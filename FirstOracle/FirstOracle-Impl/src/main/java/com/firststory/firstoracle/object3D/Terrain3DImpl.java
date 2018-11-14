/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.object.Colouring;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;

/**
 * @author n1t4chi
 */
public class Terrain3DImpl
    extends AbstractTerrain3D< Vertices3D >
{
    private UvMap uvMap;
    private Texture texture;
    private Vertices3D vertices;
    private Colouring colouring;
    private Position3DCalculator calculator;
    
    public void setColouring( Colouring colouring ) {
        this.colouring = colouring;
    }
    
    public void setUvMap( UvMap uvMap ) {
        this.uvMap = uvMap;
    }
    
    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
    
    public void setVertices( Vertices3D vertices ) {
        this.vertices = vertices;
    }
    
    public void setPositionCalculator( Position3DCalculator calculator ) { this.calculator = calculator; }
    
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
    
    @Override
    public Position3D computePosition(
        int x,
        int y,
        int z,
        Index3D arrayShift
    ) {
        return calculator.compute( x, y, z, arrayShift );
    }
}