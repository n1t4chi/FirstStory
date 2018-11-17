/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.Rotation;
import com.firststory.firstoracle.data.Scale;

/**
 * @author n1t4chi
 */
public interface MutableObjectTransformations
    < ScaleType extends Scale, RotationType extends Rotation >
    extends ObjectTransformations< ScaleType, RotationType>
{
    void setScale( ScaleType scale );
    void setRotation( RotationType rotation );
}
