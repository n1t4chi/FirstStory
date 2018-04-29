/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.TextureBuffer;
import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.data.TextureData;
import com.firststory.firstoracle.templates.IOUtilities;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
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
public final class Texture implements Closeable {
    
    private static final String FRAME_KEYWORD = "#frame#";
    private static final String DIRECITON_KEYWORD = "#direction#";
    
    /**
     * Constructs compound texture from multiple files each corresponding to one frame with only one direction.<br>
     * For more details see: (@link createCompoundTexture(String, int, int) },
     *
     * @param filePathMask mask for all files to load
     * @param frames       how many frames to represent
     * @return Texture instance
     * @throws IOException              On any error while trying to load file
     * @throws IllegalArgumentException On illegal parameters or mask being invalid to given frames/directions
     * @throws RuntimeException         When images do not have same width/height.
     */
    public static Texture createCompoundTexture( String filePathMask, int frames ) throws IOException {
        return createCompoundTexture( filePathMask, 1, frames );
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
    public static Texture createCompoundTexture( String filePathMask, int directions, int frames ) throws IOException {
        if ( frames < 1 ) {
            throw new IllegalArgumentException( "Given frame count is less than 1." );
        }
        if ( directions < 1 ) {
            throw new IllegalArgumentException( "Given direction count is less than 1." );
        }
        if ( frames > 1 && !filePathMask.contains( FRAME_KEYWORD ) ) {
            throw new IllegalArgumentException( "File path mask does not contain frame keyword" );
        }
        if ( directions > 1 && !filePathMask.contains( DIRECITON_KEYWORD ) ) {
            throw new IllegalArgumentException( "File path mask does not contain direction keyword" );
        }
        
        File[][] files = new File[frames][directions];
        for ( int frame = 0; frame < frames; frame++ ) {
            for ( int direction = 0; direction < directions; direction++ ) {
                File file = new File( replaceKeywords( filePathMask, frame, direction ) );
                if ( !file.canRead() ) {
                    throw new IOException( "File:" + file.getPath() + " does not exists!" );
                }
                files[frame][direction] = file;
            }
        }
        
        int width = -1;
        int height = -1;
        
        BufferedImage[][] images = new BufferedImage[frames][directions];
        for ( int frame = 0; frame < frames; frame++ ) {
            for ( int direction = 0; direction < directions; direction++ ) {
                BufferedImage image = ImageIO.read( files[frame][direction] );
                if ( width == -1 && height == -1 ) {
                    height = image.getHeight();
                    width = image.getWidth();
                } else {
                    if ( width != image.getWidth() && height != image.getHeight() ) {
                        throw new RuntimeException(
                            "Image:" + files[frame][direction].getPath() + " has different height/width than others!" );
                    }
                }
                images[frame][direction] = image;
            }
        }
        
        BufferedImage image = new BufferedImage( directions * width, frames * height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D graphics = ( Graphics2D ) image.getGraphics();
        for ( int frame = 0; frame < frames; frame++ ) {
            for ( int direction = 0; direction < directions; direction++ ) {
                graphics.drawImage( images[frame][direction], width * direction, height * frame, null );
            }
        }
        graphics.dispose();
        return new Texture( image, directions, frames, directions, frames );
    }
    
    private static String replaceKeywords( String filePathMask, int frame, int direction ) {
        String frameString = "" + frame;
        String directionString = "" + direction;
        return filePathMask.replace( FRAME_KEYWORD, frameString ).replace( DIRECITON_KEYWORD, directionString );
    }
    private final TextureData data;
    private final HashMap<TextureBufferLoader, TextureBuffer > buffers = new HashMap<>(  );
    
    private javafx.scene.image.Image jfxImage = null;
    /**
     * Creates object containing texture data from given image.<br>
     * Uses single frame and line count.
     *
     * @param image image to be used as texture
     * @throws IOException on problems with loading the image
     */
    public Texture( BufferedImage image ) throws IOException {
        this( image, 1, 1, 1, 1 );
    }
    
    
    
    /**
     * Creates object containing texture data from given image.
     *
     * @param image      image
     * @param directions How many directions this texture can represent.
     * @param frames     How many frames this texture represent
     * @param columns    How many columns for directions are in this texture.
     * @param rows       How many rows for frames are in this texture.
     * @throws IOException on problems with loading the image
     */
    public Texture( BufferedImage image, int directions, int frames, int columns, int rows ) throws IOException {
        this( image, image.toString(), directions, frames, columns, rows );
    }
    /**
     * Creates object containing texture data from given image.
     * @param image      image
     * @param name       Name of texture.
     * @param directions How many directions this texture can represent.
     * @param frames     How many frames this texture represent
     * @param columns    How many columns for directions are in this texture.
     * @param rows       How many rows for frames are in this texture.
     */
    public Texture( BufferedImage image, String name, int directions, int frames, int columns, int rows ) {
        if ( name == null || name.isEmpty() || frames < 1 || rows < 1 || frames > rows || directions < 1 ||
            directions > columns )
        {
            throw new IllegalArgumentException(
                "Illegal arguments for Texture.\n" + "Image:" + image + ", Frames:" + frames + ", Rows:" + rows +
                    ", Directions:" + directions + ", Columns:" + columns + "." );
        }
        ByteBuffer bf = null;
        try {
            bf = imageToByteBuffer( image );
        }catch (Exception ignore){}
        data = new TextureData( image, bf, name, directions, frames, columns, rows );
    }
    
    private static ByteBuffer imageToByteBuffer( BufferedImage image ) throws IOException {
        ByteBuffer bf;
        byte[] b;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( image, "PNG", baos );
        byte[] arr = baos.toByteArray();
        bf = ByteBuffer.allocateDirect( arr.length - 1 );
        bf.put( arr, 0, arr.length - 1 );
        bf.position( 0 );
        return bf;
    }
    
    /**
     * Creates object containing texture data from image under given path.<br>
     * Uses single frame and line count.
     *
     * @param path image file path
     * @throws IOException on problems with loading the image
     */
    public Texture( String path ) throws IOException {
        this( path, 1, 1, 1, 1 );
    }
    
    /**
     * Creates object containing texture data from image under given path.
     *
     * @param path       path to texture resource
     * @param directions How many directions this texture can represent.
     * @param frames     How many frames this texture represent
     * @param columns    How many columns for directions are in this texture.
     * @param rows       How many rows for frames are in this texture.
     * @throws IOException on problems with loading the image
     */
    public Texture( String path, int directions, int frames, int columns, int rows ) throws IOException {
        this( ImageIO.read( IOUtilities.readResource(path) ) , path,  directions, frames, columns, rows );
    }
    
    public BufferedImage getImage() {
        return data.getImage();
    }
    
    public synchronized javafx.scene.image.Image getJfxImage(){
        if(jfxImage == null){
            jfxImage = SwingFXUtils.toFXImage( getImage(), null );
        }
        return jfxImage;
    }



//    public Texture( ByteBuffer bf, String name, int directions, int frames, int columns, int rows ) throws IOException {
//        this( byteBufferToImage( bf ),bf,name,directions,frames,columns,rows );
//    }
    
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
    
    /**
     * Releases all texture resources associated with this object by calling {@link #releaseAll()}
     */
    @Override
    public void close() {
        releaseAll();
    }
    
    /**
     * Releases GPU memory resources associated with this texture.
     */
    public final void release( TextureBufferLoader loader ) {
        TextureBuffer textureBuffer = buffers.get( loader );
        if( textureBuffer != null ){
            textureBuffer.delete();
        }
    }
    
    /**
     * Binds texture for usage, if texture is not loaded then it will also load it.
     */
    public final void bind(TextureBufferLoader loader ) {
        TextureBuffer textureBuffer = buffers.computeIfAbsent( loader, this::loadNewBuffer );
        textureBuffer.bind();
    }
    
    /**
     * Loads texture data into GPU memory.<br>
     * <b>Will release previously loaded texture by this object!!!</b><br>
     * Use {@link #bind(TextureBufferLoader)}  } for reusable texture.
     */
    public final void load( TextureBufferLoader loader ) {
        release( loader );
        loadNewBuffer( loader );
    }
    
    private TextureBuffer loadNewBuffer( TextureBufferLoader loader ) {
        TextureBuffer buffer = new TextureBuffer( loader );
        buffer.create();
        buffer.load( data );
        return buffer;
    }
    
    private void releaseAll() {
        buffers.forEach( ( loader, textureBuffer ) -> textureBuffer.delete() );
        buffers.clear();
    }
}