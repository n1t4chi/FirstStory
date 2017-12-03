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
    
    default void bindCurrentUvMap( double currentTimeSnapshot, double currentCameraRotation ) {
        getUvMap().bind( getCurrentUvMapDirection( currentCameraRotation ),
            getCurrentUvMapFrame( currentTimeSnapshot )
        );
    }
    
    int getCurrentUvMapDirection( double currentCameraRotation );
    
    UvMap getUvMap();
    
    int getCurrentUvMapFrame( double currentTimeSnapshot );
    
    default int bindCurrentVerticesAndGetSize( double currentTimeSnapshot ) {
        return getVertices().bind( getCurrentVertexFrame( currentTimeSnapshot ) );
    }
    
    VerticesType getVertices();
    
    int getCurrentVertexFrame( double currentTimeSnapshot );
    
    void render( Renderer renderer );
}
