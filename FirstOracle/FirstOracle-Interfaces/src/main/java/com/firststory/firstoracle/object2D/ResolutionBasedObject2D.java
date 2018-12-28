/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.notyfying.*;
import com.firststory.firstoracle.object.*;

import java.awt.geom.Rectangle2D;

/**
 * @author n1t4chi
 */
public interface ResolutionBasedObject2D
    extends
        StaticObject2D< MutablePositionable2DTransformations, AbsolutePlane2DVertices >,
        PositionableObject2D< MutablePositionable2DTransformations, AbsolutePlane2DVertices >,
        WindowListener
{
    Rectangle2D getBounds();
    
    WindowSettings getSettings();
    
    @Override
    default UvMap getUvMap() {
        return PlaneUvMap.getPlaneUvMap();
    }
    
    @Override
    default AbsolutePlane2DVertices getVertices() {
        return AbsolutePlane2DVertices.getPlane2DVertices();
    }
    
    @Override
    default int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
    
    @Override
    default void notify( WindowSizeEvent event ) {
        updateBounds();
    }
    
    default void updateBounds() {
        var settings = getSettings();
        var bounds = getBounds();
        getTransformations().setScale( computeScale2D( bounds.getWidth(), bounds.getHeight(), settings.getWidth(), settings.getHeight() ) );
        getTransformations().setPosition( computePosition2D( bounds.getX(), bounds.getY(), settings.getWidth(), settings.getHeight() ) );
    }
    
    private Scale2D computeScale2D( double width, double height, int screenWidth, int screenHeight ) {
        var scaleX = 2 * ( float ) width / screenWidth;
        var scaleY = 2 * ( float ) height / screenHeight;
        return Scale2D.scale2( scaleX, scaleY );
    }
    
    private Position2D computePosition2D( double textPosX, double textPosY, int screenWidth, int screenHeight ) {
        var dX = 2 * ( float ) textPosX / screenWidth - 1;
        var dY = 2 * ( float ) textPosY / screenHeight - 1;
        return Position2D.pos2( dX, dY );
    }
}
