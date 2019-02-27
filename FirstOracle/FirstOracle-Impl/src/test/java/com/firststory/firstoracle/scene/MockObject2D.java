/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object2D.AbstractPositionableObject2D;
import com.firststory.firstoracle.rendering.*;

import java.util.List;

/**
 * @author n1t4chi
 */
class MockObject2D extends AbstractPositionableObject2D< MockTransformations2D, MockVertices2D > {
    
    private final MockTransformations2D transformations;
    private final MockVertices2D vertices;
    private final RenderData.RenderDataBuilder builder;
    
    MockObject2D(
        float minX,
        float maxX,
        float minY,
        float maxY
    ) {
        vertices = new MockVertices2D();
        transformations = new MockTransformations2D(
            minX,
            minY,
            maxX-minX,
            maxY-minY
        );
    
        builder = RenderData
            .renderData( RenderType.TRIANGLES )
            .setVertices( vertices )
            .setPosition( transformations.getPosition() )
            .setRotation( transformations.getRotation() )
            .setScale( transformations.getScale() );
    }
    
    @Override
    public MockTransformations2D getTransformations() {
        return transformations;
    }
    
    @Override
    public void setTransformations( MockTransformations2D transformations ) {
        throw new UnsupportedOperationException("no changing transformations");
    }
    
    @Override
    public UvMap getUvMap() {
        return null;
    }
    
    @Override
    public MockVertices2D getVertices() {
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
        return List.of( getRenderDataDirectly() );
    }
    
    RenderData getRenderDataDirectly() {
        return builder.build();
    }
}
