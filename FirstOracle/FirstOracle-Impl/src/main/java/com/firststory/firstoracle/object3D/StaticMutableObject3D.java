/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

/**
 * Class representing 3D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public class StaticMutableObject3D extends MutableObject3D {

    @Override
    public int getCurrentVertexFrame() {
        return 0;
    }

    @Override
    public int getCurrentUvMapFrame() {
        return 0;
    }

    @Override
    public int getCurrentUvMapDirection() {
        return 0;
    }
}
