/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

/**
 * @author n1t4chi
 */
public interface ObjectTransformations< Scale, Rotation, Position > {
    
    Scale getScale();
    
    Rotation getRotation();
    
    Position getPosition();
}
