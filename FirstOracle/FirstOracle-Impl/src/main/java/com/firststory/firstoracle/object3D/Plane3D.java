/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.PlaneUvMap;

/**
 * @author n1t4chi
 */
public class Plane3D< Transformations extends Object3DTransformations >
    extends StaticMutableObject3D< Transformations, Plane3DVertices >
{
    
    {
        setUvMap( PlaneUvMap.getPlaneUvMap() );
        setVertices( Plane3DVertices.getPlane3DVertices() );
    }
}
