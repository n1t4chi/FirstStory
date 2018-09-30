/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.opengl;

import com.firststory.firstoracle.FirstOracleConstants;
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
        Colouring colours,
        Position2D position,
        Scale2D scale,
        Rotation2D rotation,
        Texture texture,
        Colour overlayColour,
        Float maxAlphaChannel
    ) {
        var shaderProgram2D = context.getShaderProgram();
        var loader = context.getVertexAttributeLoader();
    
        shaderProgram2D.bindPosition( position );
        shaderProgram2D.bindRotation( rotation );
        shaderProgram2D.bindScale( scale );
    
        shaderProgram2D.bindMaxAlphaChannel( maxAlphaChannel );
        shaderProgram2D.bindOverlayColour( overlayColour );
        
        loader.bindVertices( vertices, vertexFrame );
        loader.bindUvMap( uvMap, uvDirection, uvFrame );
        loader.bindColouring( colours );
    
        var bufferSize = vertices.getVertexLength( vertexFrame );
    
        context.getTextureLoader().bind( texture );
    
        context.drawTriangles( bufferSize );
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
        var shaderProgram2D = context.getShaderProgram();
        var loader = context.getVertexAttributeLoader();
    
        shaderProgram2D.bindPosition( position );
        shaderProgram2D.bindRotation( rotation );
        shaderProgram2D.bindScale( scale );
    
        shaderProgram2D.bindMaxAlphaChannel( 1f );
        shaderProgram2D.bindOverlayColour( lineData.getColour() );
    
        loader.bindVertices( vertices, vertexFrame );
        loader.bindUvMap( FirstOracleConstants.EMPTY_UV_MAP, 0,0 );
        loader.bindColouring( FirstOracleConstants.EMPTY_COLOURING );
    
        var bufferSize = vertices.getVertexLength( vertexFrame );
    
        context.getTextureLoader().bind( FirstOracleConstants.EMPTY_TEXTURE );
    
        context.setLineWidth( lineData.getWidth() );
    
        context.drawLines( bufferSize, lineData.getType() );
    }
}
