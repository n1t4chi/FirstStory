/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.templates;

import com.firststory.firstoracle.object.Texture;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.awt.Font.TRUETYPE_FONT;
import static java.awt.Font.TYPE1_FONT;

/**
 * Class containing some IO utility methods for
 *
 * @author n1t4chi
 */
public class IOUtilities {
    
    public static java.awt.Font loadFontFromResource(
        String resourceName, boolean externalFile
    ) throws IOException {
        InputStream is;
        if ( externalFile ) {
            var file = new File( resourceName );
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
     *
     * @return Text from that wile
     *
     * @throws IOException on problems with loading the resource
     */
    public static String readTextResource( String path ) throws IOException {
        var p = Paths.get( path );
        Reader r;
    
        if ( Files.isReadable( p ) ) {
            r = new FileReader( path );
        } else {
            var resourceAsStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream( path );
            if ( resourceAsStream == null ) {
                throw new FileNotFoundException( "Could not find file:" + path );
            }
            r = new InputStreamReader( resourceAsStream );
        }
        try ( var br = new BufferedReader( r ) ) {
            var vertexSB = new StringBuilder();
            br.lines().forEach( ( s ) -> vertexSB.append( s ).append( '\n' ) );
    
            if ( vertexSB.length() > 0 ) {
                return vertexSB.substring( 0, vertexSB.length() - 1 );
            } else {
                return vertexSB.toString();
            }
        }
    }
    
    public static InputStream readResource( String resource ) throws IOException {
        var f = new File( resource );
        if( f.canRead() ){
            return new FileInputStream(f);
        }else{
            var io = Texture.class.getClassLoader().getResourceAsStream( resource );
            if ( io == null ) {
                throw new IOException( "Cannot find file: " + resource );
            }
            return io;
        }
    }
    
    public static ByteBuffer readBinaryResource( String resource ) throws IOException {
        var p = Paths.get( resource );
        ByteBuffer bf;
        if ( Files.isReadable( p ) ) {
            try ( var sbc = Files.newByteChannel( p, StandardOpenOption.READ ) ) {
                bf = ByteBuffer.allocateDirect( ( int ) ( sbc.size() ) );
                int read;
                do {
                    read = sbc.read( bf );
                } while ( read != -1 && sbc.position() != sbc.size() );
            }
        } else {
            var io = Texture.class.getClassLoader().getResourceAsStream( resource );
            if ( io == null ) {
                throw new IOException( "Cannot find file: " + resource );
            }
            var rbc = Channels.newChannel( io );
            bf = ByteBuffer.allocateDirect( 16384 );
            while ( rbc.read( bf ) != -1 ) {
                if ( bf.remaining() == 0 ) {
                    //  System.err.println("here8.0.1.2.2.3");
                    var nbf = ByteBuffer.allocateDirect( bf.capacity() * 2 );
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
