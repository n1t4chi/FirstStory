/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface NonAnimatedObject2D< Transformations extends Object2DTransformations, Vertices extends Vertices2D >
    extends Object2D< Transformations, Vertices >
{
    
    @Override
    default int getCurrentUvMapDirection( double currentCameraRotation ) {
        return 0;
    }
    
    @Override
    default int getCurrentUvMapFrame( double currentTimeSnapshot ) {
        return 0;
    }
    
}
