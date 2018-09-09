/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.Colour;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object3D.Vertices3D;
import com.firststory.firstoracle.rendering.LineData;
import com.firststory.firstoracle.rendering.Object3DRenderingContext;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public class VulkanObject3DRenderingContext implements Object3DRenderingContext {
    
    private final VulkanRenderingContext context;
    
    VulkanObject3DRenderingContext( VulkanRenderingContext context ) {
        
        this.context = context;
    }
    
    @Override
    public boolean shouldUseTextures() {
        return true;
    }
    
    @Override
    public boolean shouldDrawBorder() {
        return false;
    }
    
    @Override
    public void renderVerticesAsTriangles(
        Vertices3D vertices,
        int vertexFrame,
        UvMap uvMap,
        int uvFrame,
        int uvDirection,
        Colour colours,
        Vector3fc position,
        Vector3fc scale,
        Vector3fc rotation,
        Texture texture,
        Vector4fc overlayColour,
        Float maxAlphaChannel
    ) {
        VulkanShaderProgram3D shaderProgram3D = context.getShaderProgram3D();
        
        shaderProgram3D.bindPosition( position );
        shaderProgram3D.bindRotation( rotation );
        shaderProgram3D.bindScale( scale );
        
        shaderProgram3D.bindMaxAlphaChannel( maxAlphaChannel );
        shaderProgram3D.bindOverlayColour( overlayColour );
        
        int bufferSize = vertices.bind(
            context.getVertexAttributeLoader(),
            vertexFrame
        );
        uvMap.bind( context.getVertexAttributeLoader(), uvDirection, uvFrame );
        
        texture.bind( context.getTextureLoader() );
        
        context.drawTriangles( bufferSize );
    }
    
    @Override
    public void renderVerticesAsLines(
        Vertices3D vertices,
        int vertexFrame,
        Vector3fc position,
        Vector3fc scale,
        Vector3fc rotation,
        LineData lineLoop
    ) {
        VulkanShaderProgram3D shaderProgram3D = context.getShaderProgram3D();
        
        shaderProgram3D.bindPosition( position );
        shaderProgram3D.bindRotation( rotation );
        shaderProgram3D.bindScale( scale );
        
        shaderProgram3D.bindMaxAlphaChannel( 1f );
        shaderProgram3D.bindOverlayColour( lineLoop.getColour() );
        
        int bufferSize = vertices.bind(
            context.getVertexAttributeLoader(),
            vertexFrame
        );
        FirstOracleConstants.EMPTY_UV_MAP.bind( context.getVertexAttributeLoader(), 0, 0 );
        
        FirstOracleConstants.EMPTY_TEXTURE.bind( context.getTextureLoader() );
        
        context.setLineWidth( lineLoop.getWidth() );
        context.drawLineLoop( bufferSize );
    }
}
