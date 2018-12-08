/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.opengl;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.rendering.*;
import org.lwjgl.opengl.*;

import java.util.List;

/**
 * @author n1t4chi
 */
public class OpenGlRenderingContext implements RenderingContext {

    private final OpenGlVertexAttributeLoader attributeLoader;
    private final OpenGlTextureLoader textureLoader;
    private final OpenGlShaderProgram shaderProgram;
    
    private final boolean useTexture;
    private final boolean drawBorder;
    
    OpenGlRenderingContext(
        OpenGlVertexAttributeLoader attributeLoader,
        OpenGlTextureLoader textureLoader,
        OpenGlShaderProgram shaderProgram3D,
        boolean useTexture,
        boolean drawBorder
    ) {
        this.attributeLoader = attributeLoader;
        this.textureLoader = textureLoader;
        this.shaderProgram = shaderProgram3D;
        this.useTexture = useTexture;
        this.drawBorder = drawBorder;
    }
    
    @Override
    public void renderOverlay( Camera2D camera, List< RenderData > renderDatas ) {
        shaderProgram.bindCamera( camera.getMatrixRepresentation() );
        disableDepth();
        renderDatas.forEach( this::render );
    }
    
    @Override
    public void renderBackground( Camera2D camera, Colour backgroundColour, List< RenderData > renderDatas ) {
        shaderProgram.bindCamera( camera.getMatrixRepresentation() );
        GL11.glClearColor( backgroundColour.red(), backgroundColour.green(), backgroundColour.blue(), backgroundColour.alpha() );
        disableDepth();
        renderDatas.forEach( this::render );
    }
    
    @Override
    public void renderScene3D( Camera3D camera, List< RenderData > renderDatas ) {
        shaderProgram.bindCamera( camera.getMatrixRepresentation() );
        disableDepth();
        enableDepth();
        renderDatas.forEach( this::render );
    }
    
    @Override
    public void renderScene2D( Camera2D camera, List< RenderData > renderDatas ) {
        shaderProgram.bindCamera( camera.getMatrixRepresentation() );
        disableDepth();
        enableDepth();
        renderDatas.forEach( this::render );
    }
    
    private void render( RenderData renderData ) {
    
        shaderProgram.bindPosition( renderData.getPosition() );
        shaderProgram.bindRotation( renderData.getRotation() );
        shaderProgram.bindScale( renderData.getScale() );
        
        shaderProgram.bindMaxAlphaChannel( renderData.getMaxAlphaChannel() );
        shaderProgram.bindOverlayColour( renderData.getOverlayColour() );
        
        attributeLoader.bindVertices( renderData.getVertices(), renderData.getVertexFrame() );
        attributeLoader.bindUvMap( renderData.getUvMap(), renderData.getUvDirection(), renderData.getUvFrame() );
        attributeLoader.bindColouring( renderData.getColouring() );
        var bufferSize = renderData.getVertices().getVertexLength( renderData.getVertexFrame() );
        
        textureLoader.bind( shouldUseTextures() ? renderData.getTexture() : FirstOracleConstants.EMPTY_TEXTURE );
        
        switch ( renderData.getType() ) {
            case BORDER:
                if( !shouldDrawBorder() ) {
                    break;
                }
            case LINE_LOOP:
            case LINES:
                setLineWidth( renderData.getLineWidth() );
                drawLines( bufferSize, LineType.getLineType( renderData.getType() ) );
                break;
            case TRIANGLES:
            default:
                drawTriangles( bufferSize );
                break;
        }
    }
    
    private void setLineWidth( float width ) {
        GL11.glLineWidth( width );
    }
    
    private void drawLines( int bufferSize, LineType type ) {
        switch ( type ) {
            case LINE_LOOP:
                drawLineLoop( bufferSize );
                break;
            case LINES:
            default:
                drawLines( bufferSize );
        }
    }
    
    private void drawTriangles( int bufferSize ) {
        drawObjects( GL11.GL_TRIANGLES, bufferSize );
    }
    
    private void drawLines( int bufferSize ) {
        drawObjects( GL11.GL_LINES, bufferSize );
    }
    
    private void drawLineLoop( int bufferSize ) {
        drawObjects( GL11.GL_LINE_LOOP, bufferSize );
    }
    
    void enableVertexAttributes() {
        GL20.glEnableVertexAttribArray( 0 );
        GL20.glEnableVertexAttribArray( 1 );
        GL20.glEnableVertexAttribArray( 2 );
    }
    
    void disableVertexAttributes() {
        GL20.glDisableVertexAttribArray( 0 );
        GL20.glDisableVertexAttribArray( 1 );
        GL20.glDisableVertexAttribArray( 2 );
    }
    
    private void disableDepth() {
        GL11.glDepthMask( false );
        GL11.glDisable( GL11.GL_DEPTH_TEST );
        GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
    }
    
    private void enableDepth() {
        GL11.glDepthMask( true );
        GL11.glEnable( GL11.GL_DEPTH_TEST );
        GL11.glDepthFunc( GL11.GL_LEQUAL );
        GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
    }
    
    boolean shouldUseTextures() {
        return useTexture;
    }
    
    boolean shouldDrawBorder() {
        return drawBorder;
    }
    
    private void drawObjects( int objectType, int bufferSize ) {
        GL11.glDrawArrays( objectType, 0, bufferSize );
    }
}
