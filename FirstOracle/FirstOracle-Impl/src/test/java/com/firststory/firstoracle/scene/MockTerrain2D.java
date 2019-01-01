/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.rendering.*;

import java.util.List;

/**
 * @author n1t4chi
 */
class MockTerrain2D extends AbstractTerrain2D< MockVertices2D, IdentityPosition2DCalculator > {
    
    private final MockVertices2D vertices;
    private final RenderData.RenderDataBuilder builder;
    
    MockTerrain2D( int x, int y, Index2D shift ) {
        vertices = new MockVertices2D();
        builder = RenderData
            .renderData( RenderType.TRIANGLES )
            .setVertices( vertices )
            .setPosition( getPositionCalculator().indexToPosition( x, y, shift ) )
        ;
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
    public IdentityPosition2DCalculator getPositionCalculator() {
        return IdentityPosition2DCalculator.instance;
    }
    
    @Override
    public List< RenderData > getRenderData(
        Position position,
        double timeSnapshot,
        double cameraRotation
    ) {
        return List.of( builder
            .setPosition( position )
            .build()
        );
    }
    
    RenderData getCurrentRenderData() {
        return builder.build();
    }
}
