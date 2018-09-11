/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.opengl;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.Colour;
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
public class OpenGLObject2DRenderingContext implements Object2DRenderingContext {
    private final OpenGlRenderingContext context;
    
    OpenGLObject2DRenderingContext( OpenGlRenderingContext context ) {
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
        Colour colours,
        Vector2fc position,
        Vector2fc scale,
        Float rotation,
        Texture texture,
        Vector4fc overlayColour,
        Float maxAlphaChannel
    ) {
        OpenGlShaderProgram3D  shaderProgram2D = context.getShaderProgram();
    
        shaderProgram2D.bindPosition( position );
        shaderProgram2D.bindRotation( rotation );
        shaderProgram2D.bindScale( scale );
    
        shaderProgram2D.bindMaxAlphaChannel( maxAlphaChannel );
        shaderProgram2D.bindOverlayColour( overlayColour );
    
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
        Vertices2D vertices,
        int vertexFrame,
        Vector2fc position,
        Vector2fc scale,
        Float rotation,
        LineData lineLoop
    ) {
        OpenGlShaderProgram3D shaderProgram2D = context.getShaderProgram();
    
        shaderProgram2D.bindPosition( position );
        shaderProgram2D.bindRotation( rotation );
        shaderProgram2D.bindScale( scale );
    
        shaderProgram2D.bindMaxAlphaChannel( 1f );
        shaderProgram2D.bindOverlayColour( lineLoop.getColour() );
    
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
