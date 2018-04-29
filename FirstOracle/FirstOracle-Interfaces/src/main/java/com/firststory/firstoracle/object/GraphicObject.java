/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

/**
 * @author n1t4chi
 */
public interface GraphicObject<
        TransformationsType extends ObjectTransformations,
        BoundingBoxType extends BoundingBox,
        VerticesType extends Vertices
    >
    extends Renderable
{
    
    Texture getTexture();
    
    BoundingBoxType getBBO();
    
    TransformationsType getTransformations();
    
    default void bindCurrentUvMap( VertexAttributeLoader loader, double currentTimeSnapshot, double currentCameraRotation ) {
        getUvMap().bind( loader, getCurrentUvMapDirection( currentCameraRotation ), getCurrentUvMapFrame( currentTimeSnapshot ) );
    }
    
    int getCurrentUvMapDirection( double currentCameraRotation );
    
    UvMap getUvMap();
    
    int getCurrentUvMapFrame( double currentTimeSnapshot );
    
    default int bindCurrentVerticesAndGetSize( VertexAttributeLoader loader, double currentTimeSnapshot ) {
        return getVertices().bind( loader, getCurrentVertexFrame( currentTimeSnapshot ) );
    }
    
    VerticesType getVertices();
    
    int getCurrentVertexFrame( double currentTimeSnapshot );
}