/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.DirectionController;
import com.firststory.firstoracle.object.HexPrismUvMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;

/**
 * @author n1t4chi
 */
public class NonAnimatedHexPrism
    extends
        AbstractPositionableObject3D< MutablePositionable3DTransformations, HexPrismVertices >
    implements
        HexPrism< MutablePositionable3DTransformations >,
        NonAnimatedObject3D< MutablePositionable3DTransformations, HexPrismVertices >,
        MutableTextureObject3D< MutablePositionable3DTransformations, HexPrismVertices >,
        PositionableObject3D< MutablePositionable3DTransformations, HexPrismVertices >,
        MutableTransformationsObject3D< HexPrismVertices >
{
    private Texture texture;
    private DirectionController directionController = direction -> 0;
    
    public NonAnimatedHexPrism() {
        setTransformations( new MutablePositionable3DTransformations() );
    }
    
    
    public void setDirectionController( DirectionController directionController ) {
        this.directionController = directionController;
    }
    
    @Override
    public int getCurrentUvMapDirection( double currentCameraRotation ) {
        return directionController.getCurrentDirection( currentCameraRotation );
    }
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    @Override
    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
    
    @Override
    public UvMap getUvMap() {
        return HexPrismUvMap.getHexPrismUvMap();
    }
}
