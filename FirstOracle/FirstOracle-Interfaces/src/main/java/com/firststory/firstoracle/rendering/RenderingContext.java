/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.shader.ShaderProgram2D;
import com.firststory.firstoracle.shader.ShaderProgram3D;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface RenderingContext {
    
    void enableVertexAttributes();
    
    void disableVertexAttributes();
    
    void setBackgroundColour( Vector4fc backgroundColour );
    
    void render2D( Render< Object2DRenderingContext > context );
    
    void render3D( Render< Object3DRenderingContext > context );
    
    void useRendering2D( Camera2D camera, boolean useDepth );
    
    void useRendering3D( Camera3D camera, boolean useDepth );
    
    interface Render< T extends ObjectRenderingContext > {
        void render( T renderer );
    }
    
    //todo: delete start
    boolean getUseTexture();
    
    boolean getDrawBorder();
    
    Vector4fc getBorderColour();
    
    default void render( Runnable o ) {
        beginRender();
        o.run();
        endRender();
    }
    
    void endRender();
    
    void beginRender();
    //todo: delete end
    
    ShaderProgram2D getShaderProgram2D();
    
    ShaderProgram3D getShaderProgram3D();
    
    VertexAttributeLoader getVertexAttributeLoader();
    
    TextureBufferLoader getTextureLoader();
    
    void setLineWidth( float width );
    
    void drawLines( int bufferedAmount );
    
    void drawTriangles( int bufferSize );
    
    void drawLineLoop( int bufferSize );
    
}
