/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.opengl;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.object.data.Colour;
import com.firststory.firstoracle.rendering.LineType;
import com.firststory.firstoracle.rendering.Object2DRenderingContext;
import com.firststory.firstoracle.rendering.Object3DRenderingContext;
import com.firststory.firstoracle.rendering.RenderingContext;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * @author n1t4chi
 */
public class OpenGlRenderingContext implements RenderingContext {

    private final OpenGlVertexAttributeLoader attributeLoader;
    private final OpenGlTextureLoader textureLoader;
    private final OpenGlShaderProgram3D shaderProgram;
    
    private final Vector4f borderColour;
    private final boolean useTexture;
    private final boolean drawBorder;
    private final OpenGLObject2DRenderingContext context2D;
    private final OpenGLObject3DRenderingContext context3D;
    
    OpenGlRenderingContext(
        OpenGlVertexAttributeLoader attributeLoader,
        OpenGlTextureLoader textureLoader,
        OpenGlShaderProgram3D shaderProgram3D,
        boolean useTexture,
        boolean drawBorder,
        Vector4f borderColour
    ) {
        this.attributeLoader = attributeLoader;
        this.textureLoader = textureLoader;
        this.shaderProgram = shaderProgram3D;
        this.useTexture = useTexture;
        this.drawBorder = drawBorder;
        this.borderColour = borderColour;
        
        context2D = new OpenGLObject2DRenderingContext( this );
        context3D = new OpenGLObject3DRenderingContext( this );
    }
    
    OpenGlShaderProgram3D getShaderProgram() {
        return shaderProgram;
    }
    
    OpenGlVertexAttributeLoader getVertexAttributeLoader() {
        return attributeLoader;
    }
    
    OpenGlTextureLoader getTextureLoader() {
        return textureLoader;
    }
    
    void setLineWidth( float width ) {
        GL11.glLineWidth( width );
    }
    
    
    void drawLines( int bufferSize, LineType type ) {
        switch ( type ) {
            case LINE_LOOP:
                drawLineLoop( bufferSize );
                break;
            case LINES:
            default:
                drawLines( bufferSize );
        }
    }
    
    void drawTriangles( int bufferSize ) {
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
    }
    
    void disableVertexAttributes() {
        GL20.glDisableVertexAttribArray( 0 );
        GL20.glDisableVertexAttribArray( 1 );
    }
    
    @Override
    public void setBackgroundColour( Colour backgroundColour ) {
        GL11.glClearColor( backgroundColour.red(), backgroundColour.green(), backgroundColour.blue(), backgroundColour.alpha() );
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
    
    @Override
    public void render2D( Render< Object2DRenderingContext > context ) {
        context.render( context2D );
    }
    
    @Override
    public void render3D( Render< Object3DRenderingContext > context ) {
        context.render( context3D );
    }
    
    @Override
    public void useRendering2D( Camera2D camera, boolean useDepth ) {
        shaderProgram.bindCamera( camera.getMatrixRepresentation() );
        disableDepth();
        if( useDepth ) {
            enableDepth();
        }
    }
    
    @Override
    public void useRendering3D( Camera3D camera, boolean useDepth ) {
        shaderProgram.bindCamera( camera.getMatrixRepresentation() );
        disableDepth();
        if( useDepth ) {
            enableDepth();
        }
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