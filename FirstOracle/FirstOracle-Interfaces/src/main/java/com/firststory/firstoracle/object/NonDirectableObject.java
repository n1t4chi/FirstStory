/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.*;

public interface NonDirectableObject<
    PositionType extends Position,
    ScaleType extends Scale,
    RotationType extends Rotation,
    TransformationsType extends ObjectTransformations< ScaleType, RotationType >,
    BoundingBoxType extends BoundingBox< BoundingBoxType, ?, PositionType >,
    VerticesType extends Vertices< PositionType, BoundingBoxType >
>
    extends GraphicObject< PositionType, ScaleType, RotationType, TransformationsType, BoundingBoxType, VerticesType>
{
    
    @Override
    default int getCurrentUvMapDirection( double currentCameraRotation ) {
        return 0;
    }
}
