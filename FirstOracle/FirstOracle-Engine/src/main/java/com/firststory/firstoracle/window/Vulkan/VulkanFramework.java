/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.Vulkan;

import com.firststory.firstoracle.data.ArrayBufferLoader;
import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.rendering.RenderingCommands;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.rendering.RenderingFramework;
import com.firststory.firstoracle.shader.ShaderProgram2D;
import com.firststory.firstoracle.shader.ShaderProgram3D;

import java.io.IOException;

public class VulkanFramework implements RenderingFramework {
    
    
    @Override
    public RenderingContext getRenderingContext() {
        return null;
    }
    
    @Override
    public ShaderProgram2D getShader2D() {
        return null;
    }
    
    @Override
    public ShaderProgram3D getShader3D() {
        return null;
    }
    
    @Override
    public TextureBufferLoader getTextureLoader() {
        return null;
    }
    
    @Override
    public VertexAttributeLoader getAttributeLoader() {
        return null;
    }
    
    @Override
    public ArrayBufferLoader getBufferLoader() {
        return null;
    }
    
    @Override
    public void clearScreen() {
    
    }
    
    @Override
    public void updateViewPort( int x, int y, int width, int height ) {
    
    }
    
    @Override
    public void setCurrentCapabilitesToThisThread() {
    
    }
    
    @Override
    public void invoke( RenderingCommands commands ) throws Exception {
    
    }
    
    @Override
    public void compileShaders() throws IOException {
    
    }
    
    @Override
    public void close() throws Exception {
    
    }
}
