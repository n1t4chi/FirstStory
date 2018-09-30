/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.opengl;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.Colouring;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object.data.Colour;
import com.firststory.firstoracle.object.data.Position3D;
import com.firststory.firstoracle.object.data.Rotation3D;
import com.firststory.firstoracle.object.data.Scale3D;
import com.firststory.firstoracle.object3D.Vertices3D;
import com.firststory.firstoracle.rendering.LineData;
import com.firststory.firstoracle.rendering.Object3DRenderingContext;

/**
 * @author n1t4chi
 */
class OpenGLObject3DRenderingContext implements Object3DRenderingContext {
    private final OpenGlRenderingContext context;
    
    OpenGLObject3DRenderingContext( OpenGlRenderingContext context ) {
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
        Vertices3D vertices,
        int vertexFrame,
        UvMap uvMap,
        int uvFrame,
        int uvDirection,
        Colouring colours,
        Position3D position,
        Scale3D scale,
        Rotation3D rotation,
        Texture texture,
        Colour overlayColour,
        Float maxAlphaChannel
    ) {
        var shaderProgram3D = context.getShaderProgram();
        var loader = context.getVertexAttributeLoader();
    
        shaderProgram3D.bindPosition( position );
        shaderProgram3D.bindRotation( rotation );
        shaderProgram3D.bindScale( scale );
        
        shaderProgram3D.bindMaxAlphaChannel( maxAlphaChannel );
        shaderProgram3D.bindOverlayColour( overlayColour );
    
        loader.bindVertices( vertices, vertexFrame );
        loader.bindUvMap( uvMap, uvDirection, uvFrame );
        loader.bindColouring( colours );
    
        var bufferSize = vertices.getVertexLength( vertexFrame );
    
        context.getTextureLoader().bind( texture );
    
        context.drawTriangles( bufferSize );
    }
    
    @Override
    public void renderVerticesAsLines(
        Vertices3D vertices,
        int vertexFrame,
        Position3D position,
        Scale3D scale,
        Rotation3D rotation,
        LineData lineData
    ) {
        var shaderProgram3D = context.getShaderProgram();
        var loader = context.getVertexAttributeLoader();
    
        shaderProgram3D.bindPosition( position );
        shaderProgram3D.bindRotation( rotation );
        shaderProgram3D.bindScale( scale );
    
        shaderProgram3D.bindMaxAlphaChannel( 1f );
        shaderProgram3D.bindOverlayColour( lineData.getColour() );
    
        loader.bindVertices( vertices, vertexFrame );
        loader.bindUvMap( FirstOracleConstants.EMPTY_UV_MAP, 0,0 );
        loader.bindColouring( FirstOracleConstants.EMPTY_COLOURING );
    
        var bufferSize = vertices.getVertexLength( vertexFrame );
    
        context.getTextureLoader().bind( FirstOracleConstants.EMPTY_TEXTURE );
        
        context.setLineWidth( lineData.getWidth() );
        
        context.drawLines( bufferSize, lineData.getType() );
    }
}
