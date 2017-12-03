/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

/**
 * Class representing 3D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public class StaticMutableObject3D< Transformations extends Object3DTransformations, Vertices extends Vertices3D >
    extends MutableObject3D< Transformations, Vertices >
{

    @Override
    public int getCurrentUvMapDirection( double currentCameraRotation ) {
        return 0;
    }

    @Override
    public int getCurrentUvMapFrame( double currentTimeSnapshot ) {
        return 0;
    }

    @Override
    public int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
}
