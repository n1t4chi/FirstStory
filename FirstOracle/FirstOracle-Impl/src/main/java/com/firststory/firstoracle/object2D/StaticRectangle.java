/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class StaticRectangle
    extends
        AbstractPositionableObject2D< MutablePositionable2DTransformations, Plane2DVertices >
    implements
        Rectangle< MutablePositionable2DTransformations >,
        StaticObject2D< MutablePositionable2DTransformations, Plane2DVertices >,
        MutableTextureObject2D< MutablePositionable2DTransformations, Plane2DVertices >,
        MutableTransformationsObject2D< Plane2DVertices >
{
    
    public StaticRectangle() {
        setTransformations( new MutablePositionable2DTransformations() );
    }
    
    @Override
    public UvMap getUvMap() {
        return PlaneUvMap.getPlaneUvMap();
    }
}
