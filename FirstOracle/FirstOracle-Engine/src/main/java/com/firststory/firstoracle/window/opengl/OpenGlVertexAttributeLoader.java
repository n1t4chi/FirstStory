/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.opengl;

import com.firststory.firstoracle.data.DataBuffer;
import com.firststory.firstoracle.object.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class OpenGlVertexAttributeLoader implements VertexAttributeLoader {
    
    private final Map< Integer, DataBuffer > lastBinds = new HashMap<>( 2 );
    private final OpenGlArrayBufferLoader loader;
    
    OpenGlVertexAttributeLoader( OpenGlArrayBufferLoader loader ) {
        this.loader = loader;
    }
    
    public void close() {}
    
    @Override
    public OpenGlArrayBuffer provideBuffer( float[] array ) {
        return loader.create( array );
    }
    
    
    void bindVertices( Vertices attribute, int frame ) {
        bindBuffer( attribute, 0, 3, frame );
    }
    void bindUvMap( UvMap attribute, int direction, int frame ) {
        bindBuffer( attribute, 1, 2,  direction, frame );
    }
    void bindColouring( Colouring attribute ) {
        bindBuffer( attribute, 2, 4 );
    }
    
    private void bindBuffer( VertexAttribute attribute, int index, int size, int... parameters ) {
        DataBuffer buffer = attribute.getBuffer( this, parameters );
        DataBuffer lastBind = lastBinds.get( index );
        if ( lastBind == null || !lastBind.equals( buffer ) ) {
            buffer.bind();
            GL20.glVertexAttribPointer( index, size, GL11.GL_FLOAT, false, 0, 0 );
            lastBinds.put( index, buffer );
        }
        
    }
}
