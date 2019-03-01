/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class DirectableRectangle
    extends
        AbstractPositionableObject2D< MutablePositionable2DTransformations, Plane2DVertices >
    implements
        Rectangle< MutablePositionable2DTransformations >,
        NonAnimatedObject2D< MutablePositionable2DTransformations, Plane2DVertices >,
        DirectableObject2D< MutablePositionable2DTransformations, Plane2DVertices >,
        MutableTextureObject2D< MutablePositionable2DTransformations, Plane2DVertices >,
        MutableTransformationsObject2D< Plane2DVertices >
{
    private DirectionController directionController = direction -> 0;
    private UvMap uvMap;
    
    public DirectableRectangle() {
        setTransformations( new MutablePositionable2DTransformations() );
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
    public void setTexture( Texture texture ) {
        super.setTexture( texture );
        this.uvMap = PlaneUvMap.getPlaneUvMap(
            texture.getDirections(),
            texture.getFrames(),
            texture.getColumns(),
            texture.getRows()
        );
    }
    
    @Override
    public UvMap getUvMap() {
        return uvMap;
    }
}
