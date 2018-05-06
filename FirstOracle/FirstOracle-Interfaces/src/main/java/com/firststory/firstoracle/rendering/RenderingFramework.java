/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.data.ArrayBufferProvider;
import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.shader.ShaderProgram2D;
import com.firststory.firstoracle.shader.ShaderProgram3D;

import java.io.IOException;

public interface RenderingFramework extends AutoCloseable {
    
    RenderingContext getRenderingContext();
    
    ShaderProgram2D getShader2D();
    
    ShaderProgram3D getShader3D();
    
    TextureBufferLoader getTextureLoader();
    
    VertexAttributeLoader getAttributeLoader();
    
    ArrayBufferProvider getBufferLoader();
    
    void clearScreen();
    
    void updateViewPort( int x, int y, int width, int height );
    
    void setCurrentCapabilitesToThisThread();
    
    void invoke( RenderingCommands commands ) throws Exception;
    
    void compileShaders() throws IOException;
}
