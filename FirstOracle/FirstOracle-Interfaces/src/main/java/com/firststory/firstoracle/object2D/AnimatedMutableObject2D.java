/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.DirectionController;
import com.firststory.firstoracle.object.FrameController;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public class AnimatedMutableObject2D< Transformations extends Object2DTransformations, Vertices extends Vertices2D >
    extends MutableObject2D< Transformations, Vertices >
{
    
    DirectionController directionController;
    FrameController frameController;
    
    public DirectionController getDirectionController() {
        return directionController;
    }
    
    public void setDirectionController( DirectionController directionController ) {
        this.directionController = directionController;
    }
    
    public FrameController getFrameController() {
        return frameController;
    }
    
    public void setFrameController( FrameController frameController ) {
        this.frameController = frameController;
    }
    
    @Override
    public int getCurrentUvMapDirection( double currentCameraRotation ) {
        return getDirectionController().getCurrentDirection( currentCameraRotation );
    }
    
    @Override
    public int getCurrentUvMapFrame( double currentTimeSnapshot ) {
        return getFrameController().getCurrentFrame( currentTimeSnapshot );
    }
    
    @Override
    public int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
    
}
