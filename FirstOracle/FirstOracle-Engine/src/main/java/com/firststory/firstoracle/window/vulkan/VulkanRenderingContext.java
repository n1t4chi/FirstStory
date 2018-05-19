/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.rendering.RenderingContext;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
class VulkanRenderingContext implements RenderingContext {
    
    private final VulkanPhysicalDevice device;
    
    VulkanRenderingContext( VulkanPhysicalDevice device ) {
        this.device = device;
    }
    
    @Override
    public VulkanShaderProgram2D getShaderProgram2D() {
        return device.getShaderProgram2D();
    }
    
    @Override
    public VulkanShaderProgram3D getShaderProgram3D() {
        return device.getShaderProgram3D();
    }
    
    @Override
    public VertexAttributeLoader getVertexAttributeLoader() {
        return null;
    }
    
    @Override
    public VulkanTextureLoader getTextureLoader() {
        return device.getTextureLoader();
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
    public void enableVertexAttributes() {}
    
    @Override
    public void disableVertexAttributes() {}
    
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
