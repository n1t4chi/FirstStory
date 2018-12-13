/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class AnimatedHexagon
    extends
        AbstractPositionableObject2D< MutablePositionable2DTransformations, Hex2DVertices >
    implements
        Hexagon2D< MutablePositionable2DTransformations >,
        AnimatedObject2D< MutablePositionable2DTransformations, Hex2DVertices >,
        NonDirectableObject2D< MutablePositionable2DTransformations, Hex2DVertices >,
        MutableTextureObject2D< MutablePositionable2DTransformations, Hex2DVertices >,
        MutableTransformationsObject2D< Hex2DVertices >
{
    private Texture texture;
    private FrameController frameController = time -> 0;
    private UvMap uvMap;
    
    public AnimatedHexagon() {
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
    public Texture getTexture() {
        return texture;
    }
    
    @Override
    public void setTexture( Texture texture ) {
        this.texture = texture;
        this.uvMap = Hex2DUvMap.getHex2DUvMap(
            texture.getFrames(),
            texture.getRows()
        );
    }
    
    @Override
    public UvMap getUvMap() {
        return uvMap;
    }
    
    public void setUvMap( UvMap uvMap ) {
        this.uvMap = uvMap;
    }
}
