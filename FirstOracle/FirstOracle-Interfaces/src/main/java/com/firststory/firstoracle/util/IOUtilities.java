/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.util;

import com.firststory.firstoracle.object.Texture;
import org.lwjgl.system.CallbackI;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.awt.Font.TRUETYPE_FONT;
import static java.awt.Font.TYPE1_FONT;
import static org.lwjgl.BufferUtils.createByteBuffer;

/**
 * Class containing some IO utility methods for
 *
 * @author n1t4chi
 */
public class IOUtilities {

    public static java.awt.Font loadFontFromResource(
        String resourceName, boolean externalFile
    ) throws IOException
    {
        InputStream is;
        if ( externalFile ) {
            File file = new File( resourceName );
            is = new FileInputStream( file );
        } else {
            is = IOUtilities.class.getResourceAsStream( resourceName );
        }
        java.awt.Font font;
        try {
            font = java.awt.Font.createFont( TRUETYPE_FONT, is );
        } catch ( FontFormatException ex ) {
            try {
                font = java.awt.Font.createFont( TYPE1_FONT, is );
            } catch ( FontFormatException ex1 ) {
                throw new IOException(
                    "Invalid font file content. Error #1:\n" + ex + "\nError #2:\n" + ex1 );
            }
        }
        return font;
    }

    /**
     * Reads given text file and returns in string format.
     *
     * @param path Path to file
     * @return Text from that wile
     * @throws IOException on problems with loading the resource
     */
    public static String readTextResource( String path ) throws IOException {
        Path p = Paths.get( path );
        Reader r;

        if ( Files.isReadable( p ) ) {
            r = new FileReader( path );
        } else {
            InputStream resourceAsStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream( path );
            if ( resourceAsStream == null ) {
                throw new FileNotFoundException( "Could not find file:" + path );
            }
            r = new InputStreamReader( resourceAsStream );
        }
        try ( BufferedReader br = new BufferedReader( r ) ) {
            StringBuilder vertexSB = new StringBuilder();
            br.lines().forEach( ( s ) -> vertexSB.append( s ).append( '\n' ) );

            if ( vertexSB.length() > 0 ) {
                return vertexSB.substring( 0, vertexSB.length() - 1 );
            } else { return vertexSB.toString(); }
        }

    }

    public static ByteBuffer readBinaryResource( String resource ) throws IOException {
        Path p = Paths.get( resource );
        ByteBuffer bf;
        if ( Files.isReadable( p ) ) {
            try ( SeekableByteChannel sbc = Files.newByteChannel( p, StandardOpenOption.READ ) ) {
                bf = createByteBuffer( ( int ) ( sbc.size() - 1 ) );
                int read;
                while ( ( read = sbc.read( bf ) ) != -1 && sbc.position() + 1 != sbc.size() ) {

                }
            }
        } else {
            InputStream io = Texture.class.getClassLoader().getResourceAsStream( resource );
            if ( io == null ) {
                throw new IOException( "Cannot find file: "+resource );
            }
            ReadableByteChannel rbc = Channels.newChannel( io );
            bf = createByteBuffer( 16384 );
            while ( rbc.read( bf ) != -1 ) {
                if ( bf.remaining() == 0 ) {
                    //  System.err.println("here8.0.1.2.2.3");
                    ByteBuffer nbf = createByteBuffer( bf.capacity() * 2 );
                    bf.flip();
                    nbf.put( nbf );
                    bf = nbf;
                }
            }
        }
        bf.flip();
        return bf;
    }
}
