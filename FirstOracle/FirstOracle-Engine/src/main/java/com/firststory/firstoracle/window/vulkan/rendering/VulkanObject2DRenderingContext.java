/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.rendering;

import com.firststory.firstoracle.object.Colouring;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object2D.Vertices2D;
import com.firststory.firstoracle.rendering.LineData;
import com.firststory.firstoracle.rendering.Object2DRenderingContext;
import org.joml.Vector2fc;
import org.joml.Vector4fc;

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
        Vector2fc position,
        Vector2fc scale,
        Float rotation,
        Texture texture,
        Vector4fc overlayColour,
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
        Vector2fc position,
        Vector2fc scale,
        Float rotation,
        LineData lineData
    ) {
        context.draw( RenderData.build( RenderType.LINE_LOOP )
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
