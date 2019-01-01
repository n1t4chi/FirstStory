/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object3D.AbstractPositionableObject3D;
import com.firststory.firstoracle.rendering.RenderData;
import com.firststory.firstoracle.rendering.RenderType;

import java.util.List;

/**
 * @author n1t4chi
 */
class MockObject3D extends AbstractPositionableObject3D< MockTransformations3D, MockVertices3D > {
    
    private final MockTransformations3D transformations;
    private final MockVertices3D vertices;
    
    MockObject3D(
        float minX,
        float maxX,
        float minY,
        float maxY,
        float minZ,
        float maxZ
    ) {
        vertices = new MockVertices3D();
        transformations = new MockTransformations3D(
            minX,
            minY,
            minZ,
            maxX-minX,
            maxY-minY,
            maxZ-minZ
        );
    }
    
    @Override
    public MockTransformations3D getTransformations() {
        return transformations;
    }
    
    @Override
    public void setTransformations( MockTransformations3D transformations ) {
        throw new UnsupportedOperationException("no changing transformations");
    }
    
    @Override
    public Texture getTexture() {
        return null;
    }
    
    @Override
    public UvMap getUvMap() {
        return null;
    }
    
    @Override
    public MockVertices3D getVertices() {
        return vertices;
    }
    
    @Override
    public int getCurrentUvMapDirection( double currentCameraRotation ) {
        return 0;
    }
    
    @Override
    public int getCurrentUvMapFrame( double currentTimeSnapshot ) {
        return 0;
    }
    
    @Override
    public int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
    
    @Override
    public List< RenderData > getRenderData(
        double timeSnapshot,
        double cameraRotation
    ) {
        return List.of( RenderData.renderData( RenderType.TRIANGLES )
            .setVertices( vertices )
            .setPosition( transformations.getPosition() )
            .setRotation( transformations.getRotation() )
            .setScale( transformations.getScale() )
            .build()
        );
    }
}
