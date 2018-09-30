/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import com.firststory.firstoracle.object.Texture;

import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
public interface TextureBufferLoader< Context > {
    
    Context create();
    
    /**
     * Releases GPU memory resources associated with this texture.
     * @param texture texture to release
     */
    default void release( Texture texture ) {
        var textureBuffer = texture.extractBuffer( this );
        if( textureBuffer != null ){
            texture.removeBuffer( this );
            textureBuffer.delete();
        }
    }
    
    /**
     * Binds texture for usage, if texture is not loaded then it will also load it.
     * @param texture texture to bind
     * @return new Texture buffer
     */
    default TextureBuffer< Context > bind( Texture texture ) {
    
        var textureBuffer = texture.extractBuffer( this );
        if( textureBuffer == null ){
            textureBuffer = loadNewBuffer( texture );
            texture.putBuffer( this, textureBuffer );
        }
        textureBuffer.bind();
        return textureBuffer;
    }
    
    /**
     * Loads texture data into GPU memory.<br>
     * <b>Will release previously loaded texture by this object!!!</b><br>
     * Use {@link #bind(Texture)}  } for reusable texture.
     * @param texture texture to load
     * @return new Texture buffer
     */
    default TextureBuffer< Context > load( Texture texture ) {
        release( texture );
        return loadNewBuffer( texture );
    }
    
    default TextureBuffer< Context > loadNewBuffer( Texture texture ) {
        var buffer = new TextureBuffer<>( this );
        buffer.create();
        buffer.load( texture.getData() );
        return buffer;
    }
    
    void bindUnsafe( Context context );
    
    void loadUnsafe( Context context, ByteBuffer imageBuffer, String name ) throws BufferNotCreatedException;
    
    void deleteUnsafe( Context context );
    
    void close();
}
