/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.object.Colouring;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object.data.Colour;
import com.firststory.firstoracle.object.data.Position2D;
import com.firststory.firstoracle.object.data.Rotation2D;
import com.firststory.firstoracle.object.data.Scale2D;
import com.firststory.firstoracle.object2D.Vertices2D;
import com.firststory.firstoracle.rendering.LineData;
import com.firststory.firstoracle.rendering.Object2DRenderingContext;
import com.firststory.firstoracle.rendering.RenderType;

/**
 * @author n1t4chi
 */
public class VulkanObject2DRenderingContext implements Object2DRenderingContext {
    
    private final VulkanRenderingContext context;
    
    VulkanObject2DRenderingContext( VulkanRenderingContext context ) {
        
        this.context = context;
    }
    
    @Override
    public boolean shouldUseTextures() {
        return context.shouldUseTextures();
    }
    
    @Override
    public boolean shouldDrawBorder() {
        return context.shouldDrawBorder();
    }
    
    @Override
    public void renderVerticesAsTriangles(
        Vertices2D vertices,
        int vertexFrame,
        UvMap uvMap,
        int uvFrame,
        int uvDirection,
        Colouring colouring,
        Position2D position,
        Scale2D scale,
        Rotation2D rotation,
        Texture texture,
        Colour overlayColour,
        Float maxAlphaChannel
    ) {
        context.draw( RenderData.build( RenderType.TRIANGLES )
            .setPosition( position )
            .setRotation( rotation )
            .setScale( scale )
            .setMaxAlphaChannel( maxAlphaChannel )
            .setOverlayColour( overlayColour )
            .setVertices( vertices )
            .setVertexFrame( vertexFrame )
            .setTexture( texture )
            .setUvMap( uvMap )
            .setUvDirection( uvDirection )
            .setUvFrame( uvFrame )
            .setColouring( colouring )
            .finish()
        );
    }
    
    @Override
    public void renderVerticesAsLines(
        Vertices2D vertices,
        int vertexFrame,
        Position2D position,
        Scale2D scale,
        Rotation2D rotation,
        LineData lineData
    ) {
        context.draw( RenderData.build( lineData.getType().getRenderType() )
            .setPosition( position )
            .setRotation( rotation )
            .setScale( scale )
            .setOverlayColour( lineData.getColour() )
            .setVertices( vertices )
            .setVertexFrame( vertexFrame )
            .setLineWidth( lineData.getWidth() )
            .finish()
        );
    }
}
