/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class AnimatedAutoRotablePlane3D
    extends
        AutoRotablePlane3D
    implements
        AnimatedObject3D< MutablePositionable3DTransformations, Plane3DVertices >,
        DirectableObject3D< MutablePositionable3DTransformations, Plane3DVertices >
{
    private Texture texture;
    private DirectionController directionController = direction -> 0;
    private FrameController frameController = time -> 0;
    private UvMap uvMap;
    
    public AnimatedAutoRotablePlane3D() {
        setTransformations( new MutablePositionable3DTransformations() );
    }
    
    @Override
    public DirectionController getDirectionController() {
        return directionController;
    }
    
    public void setDirectionController( DirectionController directionController ) {
        this.directionController = directionController;
    }
    
    @Override
    public FrameController getFrameController() {
        return frameController;
    }
    
    @Override
    public void setFrameController( FrameController frameController ) {
        this.frameController = frameController;
    }
    
    @Override
    public int getCurrentUvMapDirection( double currentCameraRotation ) {
        return directionController.getCurrentDirection( currentCameraRotation );
    }
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    @Override
    public void setTexture( Texture texture ) {
        this.texture = texture;
        this.uvMap = PlaneUvMap.getPlaneUvMap(
            texture.getDirections(),
            texture.getFrames(),
            texture.getColumns(),
            texture.getRows()
        );
    }
    
    @Override
    public UvMap getUvMap() {
        return uvMap;
    }
}
