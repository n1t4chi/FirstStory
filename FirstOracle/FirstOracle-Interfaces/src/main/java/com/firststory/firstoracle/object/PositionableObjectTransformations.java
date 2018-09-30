/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.object.data.Position;
import com.firststory.firstoracle.object.data.Rotation;
import com.firststory.firstoracle.object.data.Scale;

/**
 * @author n1t4chi
 */
public interface PositionableObjectTransformations< PositionType extends Position, ScaleType extends Scale, RotationType extends Rotation > extends
    ObjectTransformations< ScaleType, RotationType >
{
    
    PositionType getPosition();
}
