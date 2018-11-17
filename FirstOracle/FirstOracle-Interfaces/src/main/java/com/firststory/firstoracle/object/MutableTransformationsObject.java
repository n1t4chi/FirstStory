/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.data.Rotation;
import com.firststory.firstoracle.data.Scale;

/**
 * @author n1t4chi
 */
public interface MutableTransformationsObject<
    PositionType extends Position,
    ScaleType extends Scale,
    RotationType extends Rotation,
    Transformations extends MutablePositionableObjectTransformations< PositionType, ScaleType, RotationType >,
    BoundingBoxType extends BoundingBox< BoundingBoxType, ?, PositionType >,
    VerticesType extends Vertices< PositionType, BoundingBoxType >
> extends
    PositionableObject< PositionType, ScaleType, RotationType, Transformations, BoundingBoxType, VerticesType >
{
    default void setPosition( PositionType position ) {
        getTransformations().setPosition( position );
    }
    
    default void setScale( ScaleType scale ) {
        getTransformations().setScale( scale );
    }
    
    default void setRotation( RotationType rotation ) {
        getTransformations().setRotation( rotation );
    }
    
}
