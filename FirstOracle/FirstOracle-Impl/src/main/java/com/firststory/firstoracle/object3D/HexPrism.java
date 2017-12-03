/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.HexPrismUvMap;

/**
 * @author n1t4chi
 */
public class HexPrism< Transformations extends Object3DTransformations >
    extends StaticMutableObject3D< Transformations, HexPrismVertices >
{
    
    {
        setUvMap( HexPrismUvMap.getHexPrismUvMap() );
        setVertices( HexPrismVertices.getHexPrismVertices() );
    }
}
