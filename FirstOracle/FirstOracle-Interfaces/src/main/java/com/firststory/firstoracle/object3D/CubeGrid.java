/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

/**
 * @author n1t4chi
 */
public interface CubeGrid extends Terrain3D< CubeVertices, CubePositionCalculator >, Cube< Identity3DTransformations > {
    
    @Override
    default CubePositionCalculator getPositionCalculator() {
        return CubePositionCalculator.instance;
    }
}
