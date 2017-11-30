/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.util.IOUtilities;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Single texture object. Colours are stored in RGBA format. <br>
 * Example: Texture with 3 faces and 3 frames requires texture of 4 faces and 4 lines<br>
 * <br>
 * ___ ___ ___ ___<br>
 * | 00| 10| 20|   |<br>
 * |___|___|___|___|<br>
 * | 01| 11| 21|   |<br>
 * |___|___|___|___|<br>
 * | 01| 11| 21|   |<br>
 * |___|___|___|___|<br>
 * |   |   |   |   |<br>
 * |___|___|___|___|<br>
 * XY - mean X face in Y frame<br>
 * <br>
 * Directions works the same as faces for Plane objects.<br>
 * Best if there would be 6 directions for hex-grid and 8 for square-grid.<br>
 * Hexagonal Prisms and Cubes will always use only 1 direction as they are already 3D, while Planes will be able to use multiple directions as they have only one face.<br>
 * <br>
 * Used are textures for cube or plane.<br>
 * Plane uses only 1 face per line while cube uses first 6 faces out of 8 in each line<br>
 * Line count must be of power of 2.
 *
 * @author n1t4chi
 */
public final class Texture implements Closeable {
    
    private static ByteBuffer imageToByteBuffer( BufferedImage image ) throws IOException {
        ByteBuffer bf;
        byte[] b;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( image, "PNG", baos );
        byte[] arr = baos.toByteArray();
        bf = ByteBuffer.allocateDirect( arr.length - 1 );
        bf.put( arr, 0, arr.length - 1 );
        
        // b = new byte[bf.remaining()];
        // bf.get(b);
        bf.position( 0 );
        //System.out.println(bf);
        
        /*File fil = new File("S:/wtf.png");
        ImageIO.write(image, "PNG", fil);
        bf = IOUtilities.readBinaryResource("S:/wtf.png");
        
        b = new byte[bf.remaining()];
        bf.mark();
        bf.get(b);
        bf.reset();
        System.out.println(bf);*/
        
        return bf;
    }
    private final int width;
    private final int height;
    private final ByteBuffer texture;
    private final String name;
    private final int frames;
    private final int directions;
    private final int rows;
    private final int columns;
    private int textureID = 0;
    
    /**
     * Creates object containing texture data from given image.<br>
     * Uses single frame and line count.
     *
     * @param image image to be used as texture
     *
     * @throws IOException on problems with loading the image
     */
    public Texture( BufferedImage image ) throws IOException {
        this( image, 1, 1, 1, 1 );
    }
    
    /**
     * Creates object containing texture data from given image.
     *
     * @param image      image
     * @param frames     How many frames this texture represent
     * @param directions How many directions this texture can represent.
     * @param rows       How many rows for frames are in this texture.
     * @param columns    How many columns for directions are in this texture.
     *
     * @throws IOException on problems with loading the image
     */
    public Texture( BufferedImage image, int frames, int directions, int rows, int columns ) throws
        IOException {
        this( imageToByteBuffer( image ), image.toString(), frames, directions, rows, columns );
    }
    
    /**
     * Creates texture out of given buffer.
     *
     * @param bf         ArrayBuffer where texture data is stored.
     * @param name       Name of texture.
     * @param frames     How many frames this texture represent
     * @param directions How many directions this texture can represent.
     * @param rows       How many rows for frames are in this texture.
     * @param columns    How many columns for directions are in this texture.
     */
    public Texture(
        ByteBuffer bf, String name, int frames, int directions, int rows, int columns
    ) {
        if ( name == null || name.isEmpty() || frames < 1 || rows < 1 || frames > rows ||
            directions < 1 || directions > columns ) {
            throw new IllegalArgumentException(
                "Illegal arguments for Texture.\n" + "Name:" + name + ", Frames:" + frames +
                    ", Rows:" + rows + ", Directions:" + directions + ", Columns:" + columns + "." );
        }
        this.name = name;
        IntBuffer w = BufferUtils.createIntBuffer( 1 );
        IntBuffer h = BufferUtils.createIntBuffer( 1 );
        IntBuffer c = BufferUtils.createIntBuffer( 1 );
    
        texture = STBImage.stbi_load_from_memory( bf, w, h, c, 4 );
        if ( texture == null ) {
            throw new RuntimeException( "Cannot load image:" + name );
        }
        width = w.get( 0 );
        height = h.get( 0 );
        this.frames = frames;
        this.rows = rows;
        this.directions = directions;
        this.columns = columns;
    
    }
    
    /**
     * Creates object containing texture data from image under given path.<br>
     * Uses single frame and line count.
     *
     * @param path image file path
     *
     * @throws IOException on problems with loading the image
     */
    public Texture( String path ) throws IOException {
        this( path, 1, 1, 1, 1 );
    }
    
    /**
     * Creates object containing texture data from image under given path.
     *
     * @param path       path to texture resource
     * @param frames     How many frames this texture represent
     * @param directions How many directions this texture can represent.
     * @param rows       How many rows for frames are in this texture.
     * @param columns    How many columns for directions are in this texture.
     *
     * @throws IOException on problems with loading the image
     */
    public Texture( String path, int frames, int directions, int rows, int columns ) throws
        IOException {
        this( IOUtilities.readBinaryResource( path ), path, frames, directions, rows, columns );
    }
    
    public int getFrames() {
        return frames;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getColumns() {
        return columns;
    }
    
    public int getDirections() {
        return directions;
    }
    
    public int getWidth() {
        return width;
    }
    
    public ByteBuffer getTexture() {
        return texture;
    }
    
    public int getHeight() {
        return height;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Releases texture resources associated with this object by calling {@link #release()}
     */
    @Override
    public void close() {
        release();
    }
    
    /**
     * Releases GPU memory resources associated with this texture.
     */
    public final void release() {
        if ( textureID > 0 ) {
            GL11.glDeleteTextures( textureID );
            textureID = 0;
        }
    }
    
    /**
     * Binds texture for usage, if texture is not loaded then it will also load it.
     */
    public final void bind() {
        if ( textureID > 0 ) {
            GL11.glBindTexture( GL11.GL_TEXTURE_2D, textureID );
        } else {
            load();
        }
        //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);
    }
    
    /**
     * Loads texture data into GPU memory.<br>
     * <b>Will release previously loaded texture by this object!!!</b><br>
     * Use {@link #bind() } for reusable texture.
     */
    public final void load() {
        release();
        textureID = GL11.glGenTextures();
        //System.err.println("TextureID:"+textureID);
        GL11.glBindTexture( GL11.GL_TEXTURE_2D, textureID );
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
    
        //repeat, could be used for giant objects.
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
    
        GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR );
        GL11.glTexParameteri( GL11.GL_TEXTURE_2D,
            GL11.GL_TEXTURE_MIN_FILTER,
            GL11.GL_LINEAR_MIPMAP_LINEAR
        );
        GL30.glGenerateMipmap( GL11.GL_TEXTURE_2D );
    }
//
//    public final boolean isTextureLoaded() {
//        return textureID > 0;
//    }
//
//    /**
//     * Returns texture ID
//     *
//     * @return returns te
//     */
//    public final int getTextureID() {
//        return textureID;
//    }

}
