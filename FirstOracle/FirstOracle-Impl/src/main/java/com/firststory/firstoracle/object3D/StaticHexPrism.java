/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class StaticHexPrism
    extends
        AbstractPositionableObject3D< MutablePositionable3DTransformations, HexPrismVertices >
    implements
        HexPrism< MutablePositionable3DTransformations >,
        StaticObject3D< MutablePositionable3DTransformations, HexPrismVertices >,
        MutableTextureObject3D< MutablePositionable3DTransformations, HexPrismVertices >,
        MutableTransformationsObject3D< HexPrismVertices >
{
    public StaticHexPrism() {
        setTransformations( new MutablePositionable3DTransformations() );
    }
    
    @Override
    public UvMap getUvMap() {
        return HexPrismUvMap.getHexPrismUvMap();
    }
}
