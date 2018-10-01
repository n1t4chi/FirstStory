/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.Rotation;
import com.firststory.firstoracle.data.Scale;

/**
 * @author n1t4chi
 */
public interface ObjectTransformations< ScaleType extends Scale, RotationType extends Rotation > {
    
    ScaleType getScale();
    
    RotationType getRotation();
}
