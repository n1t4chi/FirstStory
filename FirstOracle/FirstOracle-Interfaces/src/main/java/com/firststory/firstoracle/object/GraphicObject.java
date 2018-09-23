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
    
    default Colouring getColours() {
        return FirstOracleConstants.EMPTY_COLOURING;
    }
    
    default Vector4fc getOverlayColour() {
        return FirstOracleConstants.TRANSPARENT;
    }
    
    default LineData getLineLoop() {
        return FirstOracleConstants.YELLOW_LINE_LOOP;
    }
    
    default float getMaxAlphaChannel() {
        return 1f;
    }
}
