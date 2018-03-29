/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.OpenGL;

import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.shader.ShaderProgram2D;
import com.firststory.firstoracle.shader.ShaderProgram3D;
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
    private final ShaderProgram2D shaderProgram2D;
    private final ShaderProgram3D shaderProgram3D;
    
    private final Vector4f borderColour;
    private final boolean useTexture;
    private final boolean drawBorder;

    public OpenGlRenderingContext(
        VertexAttributeLoader attributeLoader,
        TextureBufferLoader textureLoader,
        ShaderProgram2D shaderProgram2D,
        ShaderProgram3D shaderProgram3D,
    
        boolean useTexture, boolean drawBorder, Vector4f borderColour
    )
    {
        this.attributeLoader = attributeLoader;
        this.textureLoader = textureLoader;
        this.shaderProgram2D = shaderProgram2D;
        this.shaderProgram3D = shaderProgram3D;
        this.useTexture = useTexture;
        this.drawBorder = drawBorder;
        this.borderColour = borderColour;
    }

    @Override
    public ShaderProgram2D getShaderProgram2D() {
        return shaderProgram2D;
    }

    @Override
    public ShaderProgram3D getShaderProgram3D() {
        return shaderProgram3D;
    }

    @Override
    public VertexAttributeLoader getVertexAttributeLoader() {
        return attributeLoader;
    }

    @Override
    public TextureBufferLoader getTextureLoader() {
        return textureLoader;
    }

    @Override
    public void setLineWidth( float width ) {
        GL11.glLineWidth( width );
    }

    @Override
    public void drawLines( int bufferedAmount ) {
        drawObjects( GL11.GL_LINES, bufferedAmount );
    }
    
    @Override
    public void drawTriangles( int bufferSize ) {
        drawObjects( GL11.GL_TRIANGLES, bufferSize );
    }
    
    @Override
    public void drawLineLoop( int bufferSize ) {
        drawObjects( GL11.GL_LINE_LOOP, bufferSize );
    }
    
    @Override
    public void enableVertexAttributes() {
        GL20.glEnableVertexAttribArray( 0 );
        GL20.glEnableVertexAttribArray( 1 );
    }
    
    @Override
    public void disableVertexAttributes() {
        GL20.glDisableVertexAttribArray( 0 );
        GL20.glDisableVertexAttribArray( 1 );
    }
    
    @Override
    public void setBackgroundColour( Vector4fc backgroundColour ) {
        GL11.glClearColor( backgroundColour.x(), backgroundColour.y(), backgroundColour.z(), backgroundColour.w() );
    }
    
    @Override
    public void disableDepth() {
        GL11.glDepthMask( false );
        GL11.glDisable( GL11.GL_DEPTH_TEST );
        GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
    }
    
    @Override
    public void enableDepth() {
        GL11.glDepthMask( true );
        GL11.glEnable( GL11.GL_DEPTH_TEST );
        GL11.glDepthFunc( GL11.GL_LEQUAL );
        GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
    }
    
    @Override
    public boolean getUseTexture() {
        return useTexture;
    }
    
    @Override
    public boolean getDrawBorder() {
        return drawBorder;
    }
    
    @Override
    public Vector4fc getBorderColour() {
        return borderColour;
    }
    
    private void drawObjects( int objectType, int bufferSize ) {
        GL11.glDrawArrays( objectType, 0, bufferSize );
    }
}
