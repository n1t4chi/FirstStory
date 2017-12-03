/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.Hex2DUvMap;

/**
 * @author n1t4chi
 */
public class Hexagon2D< Transformations extends Object2DTransformations >
    extends StaticMutableObject2D< Transformations, Hex2DVertices >
{
    
    {
        setUvMap( Hex2DUvMap.getHex2DUvMap() );
        setVertices( Hex2DVertices.getHex2DVertices() );
    }
}
