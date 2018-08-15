/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import java.io.IOException;

/**
 * @author n1t4chi
 */
public class TextureBuffer<Context> implements DataBuffer<TextureData > {
    
    private final TextureBufferLoader<Context> loader;
    
    public TextureBuffer( TextureBufferLoader<Context> loader ) {
        this.loader = loader;
    }
    
    private TextureData data;
    private Context context;
    private boolean isLoaded;
    
    public Context getContext() {
        return context;
    }
    
    public TextureData getData() {
        return data;
    }
    
    @Override
    public boolean isLoaded() {
        return isLoaded;
    }
    
    @Override
    public boolean isCreated() {
        return context != null;
    }
    
    @Override
    public void create() throws CannotCreateBufferException {
        context = loader.create();
    }
    
    @Override
    public void bindUnsafe() throws BufferNotCreatedException, BufferNotLoadedException {
        loader.bind( context );
    }
    
    @Override
    public void loadUnsafe( TextureData data ) throws BufferNotCreatedException {
        try {
            loader.load( context, data.getByteBuffer(), data.getName() );
            this.data = data;
            isLoaded = true;
        } catch ( IOException e ) {
            throw new BufferNotCreatedException( e );
        }
    }
    
    @Override
    public void deleteUnsafe() throws BufferNotCreatedException {
        loader.delete( context );
        context = null;
        isLoaded = false;
    }
}
