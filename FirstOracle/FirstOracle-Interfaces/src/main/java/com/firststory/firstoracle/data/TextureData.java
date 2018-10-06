/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
public class TextureData {
    
    private final String name;
    private final BufferedImage image;
    private final int width;
    private final int height;
    private final int directions;
    private final int frames;
    private final int rows;
    private final int columns;
    private ByteBuffer byteBuffer;
    
    public TextureData( BufferedImage image, ByteBuffer bf, String name, int directions, int frames, int columns, int rows ) {
        this.name = name;
        byteBuffer = bf;
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        this.frames = frames;
        this.directions = directions;
        this.rows = rows;
        this.columns = columns;
    }
    
    public String getName() {
        return name;
    }
    
    public BufferedImage getImage() {
        return image;
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
    
    public int getHeight() {
        return height;
    }
    
    public ByteBuffer getByteBuffer() throws IOException {
        if(byteBuffer == null)
            byteBuffer = imageToByteBuffer( image );
        return byteBuffer;
    }
    
    private ByteBuffer imageToByteBuffer( BufferedImage image ) throws IOException {
        ByteBuffer bf;
        byte[] b;
    
        var baos = new ByteArrayOutputStream();
        ImageIO.write( image, "PNG", baos );
        var arr = baos.toByteArray();
        bf = ByteBuffer.allocateDirect( arr.length - 1 );
        bf.put( arr, 0, arr.length - 1 );
        bf.position( 0 );
        return bf;
    }
}
