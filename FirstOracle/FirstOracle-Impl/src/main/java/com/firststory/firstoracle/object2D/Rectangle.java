/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.PlaneUvMap;

/**
 * @author n1t4chi
 */
public class Rectangle< Transformations extends Object2DTransformations >
    extends StaticMutableObject2D< Transformations, Plane2DVertices >
{
    
    {
        setUvMap( PlaneUvMap.getPlaneUvMap() );
        setVertices( Plane2DVertices.getPlane2DVertices() );
    }
}
