/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.DirectionController;
import com.firststory.firstoracle.object.FrameController;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.rendering.Object2DRenderer;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface AnimatedObject2D< Transformations extends Object2DTransformations, Vertices extends Vertices2D, Renderer extends Object2DRenderer >
    extends Object2D< Transformations, Vertices, Renderer >
{
    
    DirectionController getDirectionController();
    
    void setDirectionController( DirectionController directionController );
    
    FrameController getFrameController();
    
    void setFrameController( FrameController frameController );
    
    void setUvMap( UvMap uvMap );
    
    @Override
    default int getCurrentUvMapDirection( double currentCameraRotation ) {
        return getDirectionController().getCurrentDirection( currentCameraRotation );
    }
    
    @Override
    default int getCurrentUvMapFrame( double currentTimeSnapshot ) {
        return getFrameController().getCurrentFrame( currentTimeSnapshot );
    }
    
}
