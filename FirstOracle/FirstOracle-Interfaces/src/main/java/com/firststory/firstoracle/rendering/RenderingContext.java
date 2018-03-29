/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.shader.ShaderProgram2D;
import com.firststory.firstoracle.shader.ShaderProgram3D;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface RenderingContext {
    
    ShaderProgram2D getShaderProgram2D();
    
    ShaderProgram3D getShaderProgram3D();
    
    VertexAttributeLoader getVertexAttributeLoader();
    
    TextureBufferLoader getTextureLoader();
    
    void setLineWidth( float width );
    
    void drawLines( int bufferedAmount );
    
    void drawTriangles( int bufferSize );
    
    void drawLineLoop( int bufferSize );
    
    void enableVertexAttributes();
    
    void disableVertexAttributes();
    
    void setBackgroundColour( Vector4fc backgroundColour );
    
    void disableDepth();
    
    void enableDepth();

    boolean getUseTexture();
    boolean getDrawBorder();
    Vector4fc getBorderColour();
}
