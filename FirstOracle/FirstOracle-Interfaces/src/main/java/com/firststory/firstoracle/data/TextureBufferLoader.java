/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
public interface TextureBufferLoader<Context> extends AutoCloseable {
    Context create();
    void bind( Context context );
    void load( Context context, ByteBuffer imageBuffer, String name ) throws BufferNotCreatedException;
    void delete( Context context );
}
