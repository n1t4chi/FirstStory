/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import java.io.IOException;

/**
 * @author n1t4chi
 */
public class TextureBuffer implements DataBuffer<Integer, TextureData > {
    
    private final TextureBufferLoader loader;
    
    public TextureBuffer( TextureBufferLoader loader ) {
        this.loader = loader;
    }
    
    private TextureData data;
    private int textureID;
    private boolean isLoaded;
    
    public TextureData getData() {
        return data;
    }
    
    @Override
    public Integer getContext() {
        return textureID;
    }
    
    @Override
    public boolean isLoaded() {
        return isLoaded;
    }
    
    @Override
    public boolean isCreated() {
        return textureID != 0;
    }
    
    @Override
    public void create() throws CannotCreateBufferException {
        textureID = loader.create();
    }
    
    @Override
    public void bind() throws BufferNotCreatedException, BufferNotLoadedException {
        assertCreated();
        assertLoaded();
        loader.bind( textureID );
    }
    
    @Override
    public void load( TextureData data ) throws BufferNotCreatedException {
        try {
            loader.load( textureID, data.getByteBuffer(), data.getName() );
            this.data = data;
            isLoaded = true;
        } catch ( IOException e ) {
            throw new BufferNotCreatedException( e );
        }
    }
    
    @Override
    public void delete() throws BufferNotCreatedException {
        assertLoaded();
        loader.delete( textureID );
        textureID = 0;
        isLoaded = false;
    }
}
