/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.DirectionController;
import com.firststory.firstoracle.object.FrameController;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.rendering.PositionableObject2DRenderer;

/**
 * @author n1t4chi
 */
public class AnimatedRectangle implements Rectangle< Mutable2DTransformations, PositionableObject2DRenderer >,
    AnimatedObject2D< Mutable2DTransformations, Plane2DVertices, PositionableObject2DRenderer >,
    MutableTextureObject2D< Mutable2DTransformations, Plane2DVertices, PositionableObject2DRenderer >,
    PositionableObject2D< Mutable2DTransformations, Plane2DVertices, PositionableObject2DRenderer >
{
    
    DirectionController directionController;
    FrameController frameController;
    Texture texture;
    Mutable2DTransformations transformations;
    UvMap uvMap;
    {
        setTransformations( new Mutable2DTransformations() );
    }
    @Override
    public DirectionController getDirectionController() {
        return directionController;
    }
    
    @Override
    public void setDirectionController( DirectionController directionController ) {
        this.directionController = directionController;
    }
    
    @Override
    public FrameController getFrameController() {
        return frameController;
    }
    
    @Override
    public void setFrameController( FrameController frameController ) {
        this.frameController = frameController;
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
    public void setUvMap( UvMap uvMap ) {
        this.uvMap = uvMap;
    }
    
    @Override
    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
    
    @Override
    public Mutable2DTransformations getTransformations() {
        return transformations;
    }
    
    @Override
    public void setTransformations( Mutable2DTransformations transformations ) {
        this.transformations = transformations;
    }
}
