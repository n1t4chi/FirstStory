/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class AnimatedPlane3D
    extends
        AbstractPositionableObject3D< MutablePositionable3DTransformations, Plane3DVertices >
    implements
        Plane3D< MutablePositionable3DTransformations >,
        AnimatedObject3D< MutablePositionable3DTransformations, Plane3DVertices >,
        DirectableObject3D< MutablePositionable3DTransformations, Plane3DVertices >,
        MutableTextureObject3D< MutablePositionable3DTransformations, Plane3DVertices >,
        PositionableObject3D< MutablePositionable3DTransformations, Plane3DVertices >,
        MutableTransformationsObject3D< Plane3DVertices >
{
    private Texture texture;
    private DirectionController directionController = direction -> 0;
    private FrameController frameController = time -> 0;
    private UvMap uvMap;
    
    public AnimatedPlane3D() {
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
