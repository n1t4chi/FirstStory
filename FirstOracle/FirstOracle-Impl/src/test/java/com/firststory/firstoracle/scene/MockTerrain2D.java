/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object2D.AbstractTerrain2D;
import com.firststory.firstoracle.rendering.RenderData;
import com.firststory.firstoracle.rendering.RenderType;

import java.util.List;

/**
 * @author n1t4chi
 */
class MockTerrain2D extends AbstractTerrain2D< MockVertices2D > {
    
    private final MockVertices2D vertices;
    private final RenderData.RenderDataBuilder builder;
    
    MockTerrain2D( int x, int y, Index2D shift ) {
        vertices = new MockVertices2D();
        builder = RenderData
            .renderData( RenderType.TRIANGLES )
            .setVertices( vertices )
            .setPosition( computePosition( x, y, shift ) );
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
    public Position2D computePosition(
        int x,
        int y,
        Index2D arrayShift
    ) {
        return Position2D.pos2( x + arrayShift.x(), y + arrayShift.y()  );
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
