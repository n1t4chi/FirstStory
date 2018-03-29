/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.DirectionController;
import com.firststory.firstoracle.object.FrameController;

/**
 * Class representing 3D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface AnimatedObject3D< Transformations extends Object3DTransformations, Vertices extends Vertices3D >
    extends Object3D< Transformations, Vertices >
{
    
    DirectionController getDirectionController();
    
    void setDirectionController( DirectionController directionController );
    
    FrameController getFrameController();
    
    void setFrameController( FrameController frameController );
    
    @Override
    default int getCurrentUvMapDirection( double currentCameraRotation ) {
        return getDirectionController().getCurrentDirection( currentCameraRotation );
    }
    
    @Override
    default int getCurrentUvMapFrame( double currentTimeSnapshot ) {
        return getFrameController().getCurrentFrame( currentTimeSnapshot );
    }
    
}
