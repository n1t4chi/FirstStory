/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.rendering.GraphicObjectRenderer;

/**
 * @author n1t4chi
 */
public interface GraphicObject< TransformationsType extends ObjectTransformations, BoundingBoxType extends BoundingBox, VerticesType extends Vertices, Renderer extends GraphicObjectRenderer > {
    
    Texture getTexture();
    
    BoundingBoxType getBBO();
    
    TransformationsType getTransformations();
    
    default void bindCurrentUvMap() {
        getUvMap().bind( getCurrentUvMapDirection(), getCurrentUvMapFrame() );
    }
    
    UvMap getUvMap();
    
    int getCurrentUvMapDirection();
    
    int getCurrentUvMapFrame();
    
    default int bindCurrentVerticesAndGetSize() {
        return getVertices().bind( getCurrentVertexFrame() );
    }
    
    VerticesType getVertices();
    
    int getCurrentVertexFrame();
    
    void render( Renderer renderer );
}
