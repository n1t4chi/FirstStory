/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.rendering.LineData;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface GraphicObject<
        TransformationsType extends ObjectTransformations,
        BoundingBoxType extends BoundingBox,
        VerticesType extends Vertices
    >
{
    
    Texture getTexture();
    
    BoundingBoxType getBBO();
    
    TransformationsType getTransformations();
    
    UvMap getUvMap();
    
    VerticesType getVertices();
    
    int getCurrentUvMapDirection( double currentCameraRotation );
    
    int getCurrentUvMapFrame( double currentTimeSnapshot );
    
    int getCurrentVertexFrame( double currentTimeSnapshot );
    
    default Colour getColours() {
        return FirstOracleConstants.EMPTY_COLOUR;
    }
    
    default Vector4fc getOverlayColour() {
        return FirstOracleConstants.TRANSPARENT;
    }
    
    default LineData getLineLoop() {
        return FirstOracleConstants.RED_LINE;
    }
    
    default float getMaxAlphaChannel() {
        return 1f;
    }
    
    default void bindCurrentUvMap( VertexAttributeLoader loader, double currentTimeSnapshot, double currentCameraRotation ) {
        getUvMap().bind( loader, getCurrentUvMapDirection( currentCameraRotation ), getCurrentUvMapFrame( currentTimeSnapshot ) );
    }
    
    default int bindCurrentVerticesAndGetSize( VertexAttributeLoader loader, double currentTimeSnapshot ) {
        return getVertices().bind( loader, getCurrentVertexFrame( currentTimeSnapshot ) );
    }
}
