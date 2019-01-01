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
public class DirectableAutoRotablePlane3D
    extends
        AutoRotablePlane3D
    implements
        NonAnimatedObject3D< MutablePositionable3DTransformations, Plane3DVertices >,
        DirectableObject3D< MutablePositionable3DTransformations, Plane3DVertices >
{
    private UvMap uvMap;
    private DirectionController directionController = direction -> 0;
    
    public DirectableAutoRotablePlane3D() {
        setTransformations( new MutablePositionable3DTransformations() );
    }
    
    @Override
    public DirectionController getDirectionController() {
        return directionController;
    }
    
    @Override
    public void setDirectionController( DirectionController directionController ) {
        this.directionController = directionController;
    }
    
    @Override
    public List< RenderData > getRenderData( double timeSnapshot, double cameraRotation ) {
        getTransformations().setRotation( 0, 45 + ( float ) cameraRotation,0 );
        return super.getRenderData( timeSnapshot, cameraRotation );
    }
    
    @Override
    public void setTexture( Texture texture ) {
        super.setTexture( texture );
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
