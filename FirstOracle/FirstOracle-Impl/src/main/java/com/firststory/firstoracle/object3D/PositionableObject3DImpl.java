/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class PositionableObject3DImpl
    extends
        AbstractPositionableObject3D< MutablePositionable3DTransformations, Vertices3D >
    implements
        MutableTransformationsObject3D< Vertices3D >
{
    
    private UvMap uvMap;
    private Texture texture;
    private Vertices3D vertices;
    private Colouring colouring;
    private DirectionController directionController = direction -> 0;
    private FrameController frameController = time -> 0;
    
    public void setDirectionController( DirectionController directionController ) {
        this.directionController = directionController;
    }
    
    public void setFrameController( FrameController frameController ) {
        this.frameController = frameController;
    }
    
    public PositionableObject3DImpl() {
        setTransformations( new MutablePositionable3DTransformations() );
    }
    
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
        return directionController.getCurrentDirection( currentCameraRotation );
    }
    
    @Override
    public int getCurrentUvMapFrame( double currentTimeSnapshot ) {
        return frameController.getCurrentFrame( currentTimeSnapshot );
    }
    
    @Override
    public int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
}
