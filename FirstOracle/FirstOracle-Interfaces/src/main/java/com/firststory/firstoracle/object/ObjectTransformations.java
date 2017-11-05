/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

/**
 * @author: n1t4chi
 */
public interface ObjectTransformations< Scale, Rotation, Position > {

    Scale getScale();

    Rotation getRotation();

    Position getPosition();
}
