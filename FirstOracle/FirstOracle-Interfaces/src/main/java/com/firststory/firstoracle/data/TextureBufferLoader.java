/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
public interface TextureBufferLoader extends AutoCloseable {
    int create();
    void bind( int textureID );
    void load( int textureID, ByteBuffer imageBuffer, String name ) throws BufferNotCreatedException;
    void delete( int textureID );
}
