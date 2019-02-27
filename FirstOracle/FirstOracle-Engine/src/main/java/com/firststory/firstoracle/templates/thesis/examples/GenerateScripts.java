/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.thesis.examples;

import java.io.*;
import java.util.*;

/**
 * @author n1t4chi
 */
public class GenerateScripts {
    
    public static void main( String[] args ) {
        var dir = new File( "./FirstOracle/release/0.5-SNAPSHOT" );
        var scripts = dir.listFiles( pathname -> pathname.getName().endsWith( ".sh" ) );
        System.err.println( Arrays.toString( scripts ) );
    
        Arrays.stream( scripts )
            .map( GenerateScripts::read )
            .filter( Objects::nonNull )
            .forEach( GenerateScripts::save )
        ;
    }
    
    private static void save( Map.Entry< File, String > entry ) {
        var file = entry.getKey();
        var text = entry.getValue();
        try( var bw = new BufferedWriter( new FileWriter( file.getPath().replaceAll( "sh", "ps1" ) ) ) ) {
            bw.write( text );
        }catch ( Exception ex ) {
            System.err.println( "could not save file: " + file + " due to " + ex.getMessage() );
        }
        
    }
    
    private static Map.Entry< File, String > read( File file ) {
        try{
            return Map.entry( file, new BufferedReader( new FileReader( file ) ).lines()
                .filter( s -> !s.startsWith( "#" ) )
                .map( s -> s.replaceAll( "\\\\$", "`" ) )
                .map( s -> s.replaceAll( "linux", "win" ) )
                .map( s -> s + "\n" )
                .map( s -> s.replaceAll( ":", ";" ) )
                .reduce( String::concat )
                .orElseThrow()
            );
        }catch ( Exception ex  ) {
            System.err.println( "could not parse file: " + file + " due to " + ex.getMessage() );
            return null;
        }
    }
    
}
