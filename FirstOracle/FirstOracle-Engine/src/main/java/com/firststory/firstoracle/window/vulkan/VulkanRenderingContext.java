/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.shader.ShaderProgram2D;
import com.firststory.firstoracle.shader.ShaderProgram3D;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
class VulkanRenderingContext implements RenderingContext {
    
    @Override
    public ShaderProgram2D getShaderProgram2D() {
        return null;
    }
    
    @Override
    public ShaderProgram3D getShaderProgram3D() {
        return null;
    }
    
    @Override
    public VertexAttributeLoader getVertexAttributeLoader() {
        return null;
    }
    
    @Override
    public TextureBufferLoader getTextureLoader() {
        return null;
    }
    
    @Override
    public void setLineWidth( float width ) {
    
    }
    
    @Override
    public void drawLines( int bufferedAmount ) {
    
    }
    
    @Override
    public void drawTriangles( int bufferSize ) {
    
    }
    
    @Override
    public void drawLineLoop( int bufferSize ) {
    
    }
    
    @Override
    public void enableVertexAttributes() {
    
    }
    
    @Override
    public void disableVertexAttributes() {
    
    }
    
    @Override
    public void setBackgroundColour( Vector4fc backgroundColour ) {
    
    }
    
    @Override
    public void disableDepth() {
    
    }
    
    @Override
    public void enableDepth() {
    
    }
    
    @Override
    public boolean getUseTexture() {
        return false;
    }
    
    @Override
    public boolean getDrawBorder() {
        return false;
    }
    
    @Override
    public Vector4fc getBorderColour() {
        return null;
    }
}
