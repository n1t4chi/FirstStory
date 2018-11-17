/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.*;

/**
 * @author n1t4chi
 */
public interface GraphicObject<
    PositionType extends Position,
    ScaleType extends Scale,
    RotationType extends Rotation,
    TransformationsType extends ObjectTransformations< ScaleType, RotationType >,
    BoundingBoxType extends BoundingBox< BoundingBoxType, ?, PositionType >,
    VerticesType extends Vertices< PositionType, BoundingBoxType >
> {
    
    Texture getTexture();
    
    BoundingBoxType getBBO();
    
    TransformationsType getTransformations();
    
    UvMap getUvMap();
    
    VerticesType getVertices();
    
    int getCurrentUvMapDirection( double currentCameraRotation );
    
    int getCurrentUvMapFrame( double currentTimeSnapshot );
    
    int getCurrentVertexFrame( double currentTimeSnapshot );
    
    default Colouring getColouring() {
        return FirstOracleConstants.EMPTY_COLOURING;
    }
    
    default Colour getOverlayColour() {
        return FirstOracleConstants.TRANSPARENT;
    }
    
    default LineData getLineLoop() {
        return FirstOracleConstants.YELLOW_LINE_LOOP;
    }
    
    default float getMaxAlphaChannel() {
        return 1f;
    }
    
    default RotationType getRotation() {
        return getTransformations().getRotation();
    }
    
    default ScaleType getScale() {
        return getTransformations().getScale();
    }
}
