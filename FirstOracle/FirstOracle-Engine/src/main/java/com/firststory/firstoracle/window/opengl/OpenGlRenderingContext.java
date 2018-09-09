/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.opengl;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.rendering.Object2DRenderingContext;
import com.firststory.firstoracle.rendering.Object3DRenderingContext;
import com.firststory.firstoracle.rendering.RenderingContext;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * @author n1t4chi
 */
public class OpenGlRenderingContext implements RenderingContext {

    private final VertexAttributeLoader attributeLoader;
    private final TextureBufferLoader textureLoader;
    private final OpenGlShaderProgram2D shaderProgram2D;
    private final OpenGlShaderProgram3D shaderProgram3D;
    
    private final Vector4f borderColour;
    private final boolean useTexture;
    private final boolean drawBorder;
    private final OpenGLObject2DRenderingContext context2D;
    private final OpenGLObject3DRenderingContext context3D;
    
    OpenGlRenderingContext(
        VertexAttributeLoader attributeLoader,
        TextureBufferLoader textureLoader,
        OpenGlShaderProgram2D shaderProgram2D,
        OpenGlShaderProgram3D shaderProgram3D,
        boolean useTexture,
        boolean drawBorder,
        Vector4f borderColour
    ) {
        this.attributeLoader = attributeLoader;
        this.textureLoader = textureLoader;
        this.shaderProgram2D = shaderProgram2D;
        this.shaderProgram3D = shaderProgram3D;
        this.useTexture = useTexture;
        this.drawBorder = drawBorder;
        this.borderColour = borderColour;
        
        context2D = new OpenGLObject2DRenderingContext( this );
        context3D = new OpenGLObject3DRenderingContext( this );
    }
    
    OpenGlShaderProgram2D getShaderProgram2D() {
        return shaderProgram2D;
    }
    
    OpenGlShaderProgram3D getShaderProgram3D() {
        return shaderProgram3D;
    }
    
    VertexAttributeLoader getVertexAttributeLoader() {
        return attributeLoader;
    }
    
    TextureBufferLoader getTextureLoader() {
        return textureLoader;
    }
    
    void setLineWidth( float width ) {
        GL11.glLineWidth( width );
    }
    
    void drawLines( int bufferedAmount ) {
        drawObjects( GL11.GL_LINES, bufferedAmount );
    }
    
    void drawTriangles( int bufferSize ) {
        drawObjects( GL11.GL_TRIANGLES, bufferSize );
    }
    
    void drawLineLoop( int bufferSize ) {
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
    public void setBackgroundColour( Vector4fc backgroundColour ) {
        GL11.glClearColor( backgroundColour.x(), backgroundColour.y(), backgroundColour.z(), backgroundColour.w() );
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
        shaderProgram2D.useProgram();
        shaderProgram2D.bindCamera( camera.getMatrixRepresentation() );
        disableDepth();
        if( useDepth ) {
            enableDepth();
        }
    }
    
    @Override
    public void useRendering3D( Camera3D camera, boolean useDepth ) {
        shaderProgram3D.useProgram();
        shaderProgram3D.bindCamera( camera.getMatrixRepresentation() );
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
