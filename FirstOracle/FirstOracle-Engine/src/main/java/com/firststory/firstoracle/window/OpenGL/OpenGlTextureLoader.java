/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.OpenGL;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import com.firststory.firstoracle.data.TextureBufferLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author n1t4chi
 */
public class OpenGlTextureLoader implements TextureBufferLoader {
    
    private HashSet<Integer> textures = new HashSet<>();
    
    @Override
    public void close() {
        new ArrayList<>( textures ).forEach( this::delete );
    }
    
    @Override
    public int create(){
        int textureID = GL11.glGenTextures();
        if( textureID <= 0 ) {
            throw new CannotCreateBufferException();
        }
        textures.add( textureID );
        return textureID;
    }
    
    @Override
    public void bind( int textureID ) {
        GL11.glBindTexture( GL11.GL_TEXTURE_2D, textureID );
    }
    
    @Override
    public void load( int textureID, ByteBuffer imageBuffer, String name ) throws BufferNotCreatedException {
        bind( textureID );
        IntBuffer w = BufferUtils.createIntBuffer( 1 );
        IntBuffer h = BufferUtils.createIntBuffer( 1 );
        IntBuffer c = BufferUtils.createIntBuffer( 1 );
        ByteBuffer texture = STBImage.stbi_load_from_memory( imageBuffer, w, h, c, 4 );
        if ( texture == null ) {
            throw new BufferNotCreatedException( "Cannot load image:" + name );
        }
        int width = w.get( 0 );
        int height = h.get( 0 );
        GL11.glTexImage2D( GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_RGBA,
            width,
            height,
            0,
            GL11.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE, texture
        );
        
        //repeat, could be used for giant objects.
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        
        GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR );
        GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR );
        GL30.glGenerateMipmap( GL11.GL_TEXTURE_2D );
    }
    
    public void delete( int textureID ) {
        textures.remove( textureID );
        GL11.glDeleteTextures( textureID );
    }
}
