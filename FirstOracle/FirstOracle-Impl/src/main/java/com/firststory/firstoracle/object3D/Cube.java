/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.CubeUvMap;

/**
 * @author n1t4chi
 */
public class Cube< Transformations extends Object3DTransformations >
    extends StaticMutableObject3D< Transformations, CubeVertices >
{
    
    {
        setUvMap( CubeUvMap.getCubeUvMap() );
        setVertices( CubeVertices.getCubeVertices() );
    }
}
