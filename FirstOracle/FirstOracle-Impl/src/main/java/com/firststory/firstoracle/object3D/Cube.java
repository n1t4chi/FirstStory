/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.CubeUvMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;

/**
 * @author n1t4chi
 */
public class Cube implements Object3D {

    final UvMap map = CubeUvMap.getCubeUvMap();
    final Vertices3D vertices = CubeVertices.getCubeVertices();
    private Texture texture;
    private ObjectTransformations3D transformations;

    public Cube(
        Texture texture, ObjectTransformations3D transformations
    )
    {
        this.texture = texture;
        this.transformations = transformations;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public UvMap getUvMap() {
        return map;
    }

    @Override
    public Vertices3D getVertices() {
        return vertices;
    }

    @Override
    public ObjectTransformations3D getTransformations() {
        return transformations;
    }

    public void setTransformations( ObjectTransformations3D transformations ) {
        this.transformations = transformations;
    }

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

    public void setTexture(Texture texture) {
        this.texture=texture;
    }
}
