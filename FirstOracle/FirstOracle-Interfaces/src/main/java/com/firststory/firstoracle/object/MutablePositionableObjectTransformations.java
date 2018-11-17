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
public interface MutablePositionableObjectTransformations
    < PositionType extends Position, ScaleType extends Scale, RotationType extends Rotation >
    extends
        PositionableObjectTransformations< PositionType, ScaleType, RotationType>,
        MutableObjectTransformations< ScaleType, RotationType>
{
    void setPosition( PositionType position );
}
