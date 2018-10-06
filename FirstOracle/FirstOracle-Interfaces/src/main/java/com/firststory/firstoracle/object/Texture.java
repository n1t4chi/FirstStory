/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.buffer.TextureBuffer;
import com.firststory.firstoracle.buffer.TextureBufferLoader;
import com.firststory.firstoracle.data.TextureData;
import com.firststory.firstoracle.templates.IOUtilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

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
 * Hexagonal Prisms and Cubes will always use only 1 direction as they are already 3D,
 * while Planes will be able to use multiple directions as they have only one face.<br>
 * <br>
 * Used are textures for cube or plane.<br>
 * Plane uses only 1 face per line while cube uses first 6 faces out of 8 in each line<br>
 * Line count must be of power of 2.
 *
 * @author n1t4chi
 */
public final class Texture {
    
    private static final String FRAME_KEYWORD = "#frame#";
    private static final String DIRECTION_KEYWORD = "#direction#";
    
    public static Texture createText( String text ) throws IOException {
    
        var image = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
        
        return create( image );
    }
    
    public static Texture createCompound( String filePathMask, int frames ) throws IOException {
        return createCompound( filePathMask, 1, frames );
    }
    
    /**
     * Constructs compound texture from multiple files each corresponding to one frame in given direction.<br>
     * File path mask must express file paths that would apply to all needed files to construct texture.
     * Mask also must specify keywords: '#frame#' and '#direction#'
     * if corresponding frame or direction count is greater than one.<br>
     * For example: './resources/texture_#frame#_#direction#.jpg'<br>
     * This method will take all files with such mask and try to load all possible combinations of frames/directions
     * starting from (0,0) and finishing at (frames-1,direction-1)<br>
     * for example given mask: ./file_#frame#_#direction# and 10 frames and 2 direction
     * this method will try to find files starting with './file_0_0' and ending with "./file_9_1'.
     * <p>
     * Also all
     *
     * @param filePathMask mask for all files to load
     * @param directions   how many directions to represent
     * @param frames       how many frames to represent
     * @return Texture instance
     * @throws IOException              On any error while trying to load file
     * @throws IllegalArgumentException On illegal parameters or mask being invalid to given frames/directions
     * @throws RuntimeException         When images do not have same width/height.
     */
    public static Texture createCompound( String filePathMask, int directions, int frames ) throws IOException {
        if ( frames < 1 ) {
            throw new IllegalArgumentException( "Given frame count is less than 1." );
        }
        if ( directions < 1 ) {
            throw new IllegalArgumentException( "Given direction count is less than 1." );
        }
        if ( frames > 1 && !filePathMask.contains( FRAME_KEYWORD ) ) {
            throw new IllegalArgumentException( "File path mask does not contain frame keyword" );
        }
        if ( directions > 1 && !filePathMask.contains( DIRECTION_KEYWORD ) ) {
            throw new IllegalArgumentException( "File path mask does not contain direction keyword" );
        }
        
        var inputStreams = new InputStream[frames][directions];
        for ( var frame = 0; frame < frames; frame++ ) {
            for ( var direction = 0; direction < directions; direction++ ) {
                var path = replaceKeywords( filePathMask, frame, direction );
                var file = new File( path );
                if ( file.canRead() ) {
                    inputStreams[frame][direction] = file.toURI().toURL().openStream();
                }else{
                    inputStreams[frame][direction] = Texture.class.getClassLoader().getResourceAsStream( path );
                    if(inputStreams[frame][direction] == null){
                        throw new IOException( "Image:" + path + " does not exists!" );
                    }
                }
            }
        }
        
        var width = -1;
        var height = -1;
        
        var images = new BufferedImage[frames][directions];
        for ( var frame = 0; frame < frames; frame++ ) {
            for ( var direction = 0; direction < directions; direction++ ) {
                var image = ImageIO.read( inputStreams[frame][direction] );
                if ( width == -1 && height == -1 ) {
                    height = image.getHeight();
                    width = image.getWidth();
                } else {
                    if ( width != image.getWidth() && height != image.getHeight() ) {
                        throw new RuntimeException(
                            "Image:" + replaceKeywords( filePathMask, frame, direction ) +
                                " has different height/width than others!" );
                    }
                }
                images[frame][direction] = image;
            }
        }
        
        var image = new BufferedImage(
            directions * width, frames * height, BufferedImage.TYPE_INT_ARGB );
        var graphics = ( Graphics2D ) image.getGraphics();
        for ( var frame = 0; frame < frames; frame++ ) {
            for ( var direction = 0; direction < directions; direction++ ) {
                graphics.drawImage( images[frame][direction], width * direction, height * frame, null );
            }
        }
        graphics.dispose();
        return create( image, directions, frames, directions, frames );
    }
    
    public static Texture create( BufferedImage image ) {
        return create( image, 1, 1, 1, 1 );
    }
    
    public static Texture create( BufferedImage image, int directions, int frames, int columns, int rows ) {
        return create( image, image.toString(), directions, frames, columns, rows );
    }
    
    public static Texture create( String path ) throws IOException {
        return create( path, 1, 1, 1, 1 );
    }
    
    public static Texture create( String path, int directions, int frames, int columns, int rows ) throws IOException {
        return create( ImageIO.read( IOUtilities.readResource(path) ) , path,  directions, frames, columns, rows );
    }
    
    public static Texture create( BufferedImage image, String name, int directions, int frames, int columns, int rows ) {
        if (
            name == null || name.isEmpty() ||
            frames < 1 || rows < 1 ||
            frames > rows ||
            directions < 1 ||
            directions > columns
        ) {
            throw new IllegalArgumentException(
                "Illegal arguments for Texture.\n" +
                    "Image:" + image +
                    ", Frames:" + frames +
                    ", Rows:" + rows +
                    ", Directions:" + directions +
                    ", Columns:" + columns +
                "." );
        }
        try {
            return new Texture( new TextureData(
                image,
                imageToByteBuffer( image ),
                name,
                directions,
                frames,
                columns,
                rows
            ) );
        } catch ( Exception ex ) {
            throw new RuntimeException( "Exception of parsing the image to byte buffer:", ex );
        }
    }
    
    private static String replaceKeywords( String filePathMask, int frame, int direction ) {
        var frameString = "" + frame;
        var directionString = "" + direction;
        return filePathMask.replace( FRAME_KEYWORD, frameString ).replace( DIRECTION_KEYWORD, directionString );
    }
    
    private static ByteBuffer imageToByteBuffer( BufferedImage image ) throws IOException {
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
    private final TextureData data;
    
    private final HashMap<TextureBufferLoader< ? >, TextureBuffer< ? > > buffers = new HashMap<>(  );
    
    public Texture( TextureData textureData ) {
        data = textureData;
    }
    
    public BufferedImage getImage() {
        return data.getImage();
    }
    
    public int getFrames() {
        return data.getFrames();
    }
    
    public int getRows() {
        return data.getRows();
    }
    
    public int getColumns() {
        return data.getColumns();
    }
    
    public int getDirections() {
        return data.getDirections();
    }
    
    public int getWidth() {
        return data.getWidth();
    }
    
    public int getHeight() {
        return data.getHeight();
    }
    
    public String getName() {
        return data.getName();
    }
    
    @SuppressWarnings( "unchecked" )
    public < Context > TextureBuffer< Context > putBuffer( TextureBufferLoader<Context> loader, TextureBuffer<Context> buffer  ) {
        return ( TextureBuffer< Context > ) buffers.put( loader, buffer );
    }
    
    @SuppressWarnings( "unchecked" )
    public <Context> TextureBuffer<Context> extractBuffer( TextureBufferLoader<Context> loader ) {
        return ( TextureBuffer< Context > ) buffers.get( loader );
    }
    
    /**
     * Releases all texture resources associated with this object by calling {@link #releaseAll()}
     */
    public void dispose() {
        releaseAll();
    }
    
    public TextureData getData() {
        return data;
    }
    
    public void removeBuffer( TextureBufferLoader< ? > loader ) {
        buffers.remove( loader );
    }
    
    private void releaseAll() {
        buffers.forEach( ( loader, textureBuffer ) -> textureBuffer.delete() );
        buffers.clear();
    }
}
