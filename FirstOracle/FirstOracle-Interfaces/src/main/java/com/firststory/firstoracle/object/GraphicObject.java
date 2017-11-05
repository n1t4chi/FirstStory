/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

/**
 * @author: n1t4chi
 */
public interface GraphicObject< TransformationsType extends ObjectTransformations, BoundingBoxType extends BoundingBox, VerticesType extends Vertices > {

    Texture getTexture();

    UvMap getUvMap();

    BoundingBoxType getBBO();

    VerticesType getVertices();

    TransformationsType getTransformations();

    int getCurrentVertexFrame();

    int getCurrentUvMapFrame();

    int getCurrentUvMapDirection();

    default void bindCurrentUvMap() {
        getUvMap().bind( getCurrentUvMapDirection(), getCurrentUvMapFrame() );
    }

    default int bindCurrentVerticesAndGetSize() {
        return getVertices().bind( getCurrentVertexFrame() );
    }
}
