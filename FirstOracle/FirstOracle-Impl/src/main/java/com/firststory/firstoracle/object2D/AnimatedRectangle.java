/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class AnimatedRectangle
    extends
        AbstractPositionableObject2D< MutablePositionable2DTransformations, Plane2DVertices >
    implements
        Rectangle< MutablePositionable2DTransformations >,
        AnimatedObject2D< MutablePositionable2DTransformations, Plane2DVertices >,
        NonDirectableObject2D< MutablePositionable2DTransformations, Plane2DVertices >,
        MutableTextureObject2D< MutablePositionable2DTransformations, Plane2DVertices >,
        MutableTransformationsObject2D< Plane2DVertices >
{
    private FrameController frameController = time -> 0;
    private UvMap uvMap;
    
    public AnimatedRectangle() {
        setTransformations( new MutablePositionable2DTransformations() );
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
    public UvMap getUvMap() {
        return uvMap;
    }
    
    @Override
    public void setTexture( Texture texture ) {
        super.setTexture( texture );
        this.uvMap = PlaneUvMap.getPlaneUvMap(
            texture.getDirections(),
            texture.getFrames(),
            texture.getColumns(),
            texture.getRows()
        );
    }
}
