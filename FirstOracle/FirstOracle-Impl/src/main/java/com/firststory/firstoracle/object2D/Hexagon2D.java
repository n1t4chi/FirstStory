/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.Hex2DUvMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;

/**
 * @author n1t4chi
 */
public class Hexagon2D implements Object2D {

    final UvMap map = Hex2DUvMap.getHex2DUvMap();
    final Vertices2D vertices = Hex2DVertices.getHex2DVertices();
    private Texture texture;
    private ObjectTransformations2D transformations;

    public Hexagon2D( Texture texture, ObjectTransformations2D transformations ) {
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
    public Vertices2D getVertices() {
        return vertices;
    }

    @Override
    public ObjectTransformations2D getTransformations() {
        return transformations;
    }

    public void setTransformations( ObjectTransformations2D transformations ) {
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

    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
}
