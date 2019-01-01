/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class PositionableObject2DImpl
    extends
        AbstractPositionableObject2D< MutablePositionable2DTransformations, Vertices2D >
    implements
        MutableTransformationsObject2D< Vertices2D >
{
    
    private UvMap uvMap = PlaneUvMap.getPlaneUvMap();
    private Vertices2D vertices = AbsolutePlane2DVertices.getPlane2DVertices();
    private DirectionController directionController = direction -> 0;
    private FrameController frameController = time -> 0;
    
    public void setDirectionController( DirectionController directionController ) {
        this.directionController = directionController;
    }
    
    public void setFrameController( FrameController frameController ) {
        this.frameController = frameController;
    }
    
    public PositionableObject2DImpl() {
        setTransformations( new MutablePositionable2DTransformations() );
    }
    
    public void setUvMap( UvMap uvMap ) {
        this.uvMap = uvMap;
    }
    
    public void setVertices( Vertices2D vertices ) {
        this.vertices = vertices;
    }
    
    @Override
    public UvMap getUvMap() {
        return uvMap;
    }
    
    @Override
    public Vertices2D getVertices() {
        return vertices;
    }
    
    @Override
    public int getCurrentUvMapDirection( double currentCameraRotation ) {
        return directionController.getCurrentDirection( currentCameraRotation );
    }
    
    @Override
    public int getCurrentUvMapFrame( double currentTimeSnapshot ) {
        return frameController.getCurrentFrame( currentTimeSnapshot );
    }
    
    @Override
    public int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
}
