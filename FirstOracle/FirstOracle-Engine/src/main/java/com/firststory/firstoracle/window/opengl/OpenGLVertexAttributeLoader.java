/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.opengl;

import com.firststory.firstoracle.data.ArrayBuffer;
import com.firststory.firstoracle.data.ArrayBufferLoader;
import com.firststory.firstoracle.data.DataBuffer;
import com.firststory.firstoracle.object.VertexAttribute;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class OpenGLVertexAttributeLoader implements VertexAttributeLoader, AutoCloseable {
    
    private final Map< Integer, Integer > lastBinds = new HashMap<>( 2 );
    private final HashSet<ArrayBuffer> buffers = new HashSet<>();
    private final ArrayBufferLoader loader;
    
    public OpenGLVertexAttributeLoader( ArrayBufferLoader loader ) {
        this.loader = loader;
    }
    
    @Override
    public void close() {
        buffers.forEach( DataBuffer::close );
        buffers.clear();
    }
    
    @Override
    public ArrayBuffer createEmptyBuffer() {
        return new ArrayBuffer( loader );
    }
    
    @Override
    public void bindBuffer( VertexAttribute attribute, long key ) {
        ArrayBuffer buffer = attribute.getBuffer( key, this );
        int index = attribute.getIndex();
        Integer lastBind = lastBinds.get( index );
        if ( lastBind == null || !lastBind.equals( buffer.getID() ) ) {
            buffer.bind();
            GL20.glVertexAttribPointer( index, attribute.getVertexSize(), GL11.GL_FLOAT, false, 0, 0 );
            lastBinds.put( index, buffer.getID() );
        }
        
    }
}
