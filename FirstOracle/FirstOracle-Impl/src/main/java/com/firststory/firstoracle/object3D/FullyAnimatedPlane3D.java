/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.List;

/**
 * @author n1t4chi
 */
public class FullyAnimatedPlane3D
    extends
        SimplePlane3D
    implements
        AnimatedObject3D< MutablePositionable3DTransformations, Plane3DVertices >,
        DirectableObject3D< MutablePositionable3DTransformations, Plane3DVertices >
{
    private FrameController frameController = time -> 0;
    private DirectionController directionController = direction -> 0;
    private Texture texture;
    private UvMap uvMap;
    
    public FullyAnimatedPlane3D() {
        setTransformations( new MutablePositionable3DTransformations() );
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
    public DirectionController getDirectionController() {
        return directionController;
    }
    
    public void setDirectionController( DirectionController directionController ) {
        this.directionController = directionController;
    }
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    @Override
    public UvMap getUvMap() {
        return uvMap;
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
    public List< RenderData > getRenderData( double timeSnapshot, double cameraRotation ) {
        getTransformations().setRotation( 0, 45 + ( float ) cameraRotation,0 );
        return super.getRenderData( timeSnapshot, cameraRotation );
    }
}
