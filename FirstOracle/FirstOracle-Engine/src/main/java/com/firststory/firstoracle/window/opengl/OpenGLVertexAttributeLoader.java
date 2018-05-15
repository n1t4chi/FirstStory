/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.opengl;

import com.firststory.firstoracle.data.ArrayBuffer;
import com.firststory.firstoracle.object.VertexAttribute;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class OpenGLVertexAttributeLoader implements VertexAttributeLoader {
    
    private final Map< Integer, ArrayBuffer > lastBinds = new HashMap<>( 2 );
    private final OpenGlArrayBufferLoader loader;
    
    OpenGLVertexAttributeLoader( OpenGlArrayBufferLoader loader ) {
        this.loader = loader;
    }
    
    public void close() {}
    
    @Override
    public OpenGlArrayBuffer createEmptyBuffer() {
        return new OpenGlArrayBuffer( loader );
    }
    
    @Override
    public void bindBuffer( VertexAttribute attribute, long key ) {
        ArrayBuffer buffer = attribute.getBuffer( key, this );
        int index = attribute.getIndex();
        ArrayBuffer lastBind = lastBinds.get( index );
        if ( lastBind == null || !lastBind.equals( buffer ) ) {
            buffer.bind();
            GL20.glVertexAttribPointer( index, attribute.getVertexSize(), GL11.GL_FLOAT, false, 0, 0 );
            lastBinds.put( index, buffer );
        }
        
    }
}
