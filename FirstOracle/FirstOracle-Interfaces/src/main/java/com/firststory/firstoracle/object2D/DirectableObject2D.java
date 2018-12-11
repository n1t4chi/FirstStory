/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.DirectionController;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface DirectableObject2D< Transformations extends Object2DTransformations, Vertices extends Vertices2D >
    extends Object2D< Transformations, Vertices >
{
    
    DirectionController getDirectionController();
    
    void setDirectionController( DirectionController directionController );
    
    @Override
    default int getCurrentUvMapDirection( double currentCameraRotation ) {
        return getDirectionController().getCurrentDirection( currentCameraRotation );
    }
}
