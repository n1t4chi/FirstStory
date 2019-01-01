/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.opengl;

import com.firststory.firstoracle.buffer.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author n1t4chi
 */
public class OpenGlTextureLoader implements TextureBufferLoader<Integer> {
    
    private final HashSet<Integer> textures = new HashSet<>();
    
    public void dispose() {
        new ArrayList<>( textures ).forEach( this::deleteUnsafe );
    }
    
    @Override
    public Integer create(){
        var textureID = GL11.glGenTextures();
        if( textureID <= 0 ) {
            throw new CannotCreateBufferException();
        }
        textures.add( textureID );
        return textureID;
    }
    
    @Override
    public void bindUnsafe( Integer textureID ) {
        GL11.glBindTexture( GL11.GL_TEXTURE_2D, textureID );
    }
    
    @Override
    public void loadUnsafe( Integer textureID, ByteBuffer imageBuffer, String name ) throws BufferNotCreatedException {
        bindUnsafe( textureID );
        var w = BufferUtils.createIntBuffer( 1 );
        var h = BufferUtils.createIntBuffer( 1 );
        var c = BufferUtils.createIntBuffer( 1 );
        var texture = STBImage.stbi_load_from_memory( imageBuffer, w, h, c, 4 );
        if ( texture == null ) {
            throw new BufferNotCreatedException( "Cannot load image:" + name );
        }
        var width = w.get( 0 );
        var height = h.get( 0 );
        GL11.glTexImage2D( GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_RGBA,
            width,
            height,
            0,
            GL11.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE,
            texture
        );
        
        //todo: repeat, could be used for giant objects.
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        
//        GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST );
//        GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST );
        GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR );
        GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR );
        GL30.glGenerateMipmap( GL11.GL_TEXTURE_2D );
    }
    
    public void deleteUnsafe( Integer textureID ) {
        textures.remove( textureID );
        GL11.glDeleteTextures( textureID );
    }
}