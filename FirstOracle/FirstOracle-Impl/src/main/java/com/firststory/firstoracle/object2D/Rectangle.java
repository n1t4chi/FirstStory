/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.PlaneUvMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;

/**
 * @author n1t4chi
 */
public class Rectangle implements Object2D {
    private Texture texture;
    private ObjectTransformations2D transformations;

    public Rectangle(
        Texture texture, ObjectTransformations2D transformations
    )
    {
        this.texture = texture;
        this.transformations = transformations;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public ObjectTransformations2D getTransformations() {
        return transformations;
    }

    public void setTransformations( ObjectTransformations2D transformations ) {
        this.transformations = transformations;
    }

    final UvMap map = PlaneUvMap.getPlaneUvMap( 1,1,1,1 );
    final Vertices2D vertices = Plane2DVertices.getPlane2DVertices();

    @Override
    public UvMap getUvMap() {
        return map;
    }

    @Override
    public Vertices2D getVertices() {
        return vertices;
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
}
