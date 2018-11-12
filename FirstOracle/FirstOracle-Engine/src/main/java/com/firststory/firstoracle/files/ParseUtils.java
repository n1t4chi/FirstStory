/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files;

import com.firststory.firstoracle.files.Exceptions.InvalidValueException;
import com.firststory.firstoracle.files.Exceptions.ParseFailedException;
import org.joml.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author n1t4chi
 */
public interface ParseUtils {
    
    String PARAM_CLASS = "class";
    String PARAM_POSITION = "position";
    String PARAM_ROTATION = "rotation";
    String PARAM_SCALE = "scale";
    String PARAM_TEXTURE = "texture";
    String PARAM_UV = "uv";
    String PARAM_VERTICES = "vertices";
    String PARAM_COLOURING = "colouring";
    
    String METHOD_SET_TEXTURE = "setTexture";
    String METHOD_SET_COLOURING = "setColouring";
    String METHOD_SET_UV_MAP = "setUvMap";
    String METHOD_SET_VERTICES = "setVertices";
    
    String NAME_OBJECTS_2D = "objects2d";
    String NAME_TERRAIN_2D = "terrains2d";
    String NAME_OBJECTS_3D = "objects3d";
    String NAME_TERRAIN_3D = "terrains3d";
    
    String FLOAT = "(\\d+(\\.\\d+)?)";
    String DIV = ",";
    Pattern PatternVec4 = Pattern.compile( FLOAT + DIV + FLOAT + DIV + FLOAT + DIV + FLOAT );
    Pattern PatternVec3 = Pattern.compile( FLOAT + DIV + FLOAT + DIV + FLOAT );
    Pattern PatternVec2 = Pattern.compile( FLOAT + DIV + FLOAT );
    Pattern PatternVec1 = Pattern.compile( FLOAT );
    
    static String indent( String node ) {
        return node.replaceAll( "\n", "\n\t" );
    }
    
    static float toVec1( String text ) {
        return toVec( 
            text, 
            PatternVec1, 
            matcher -> Float.parseFloat( matcher.group( 1 ) ), 
            "float"
        );
    }
    
    static Vector2fc toVec2( String text ) {
        return toVec(
            text,
            PatternVec2,
            matcher -> new Vector2f( 
                Float.parseFloat( matcher.group( 1 ) ), 
                Float.parseFloat( matcher.group( 3 ) )
            ),
            "Vector 2D"
        );
    }
    
    static Vector3fc toVec3( String text ) {
        return toVec(
            text,
            PatternVec3,
            matcher -> new Vector3f(
                Float.parseFloat( matcher.group( 1 ) ),
                Float.parseFloat( matcher.group( 3 ) ),
                Float.parseFloat( matcher.group( 5 ) )
            ),
            "Vector 3D"
        );
    }
    
    static Vector4fc toVec4( String text ) {
        return toVec(
            text,
            PatternVec4,
            matcher -> new Vector4f(
                Float.parseFloat( matcher.group( 1 ) ),
                Float.parseFloat( matcher.group( 3 ) ),
                Float.parseFloat( matcher.group( 3 ) ),
                Float.parseFloat( matcher.group( 7 ) )
            ),
            "Vector 4D"
        );
    }
    
    static <T> T toVec(
        String text,
        Pattern pattern,
        Function< Matcher, T > parse,
        String valueType
    ) {
        text = text.replaceAll( "[{} ]", "" );
        var matcher = pattern.matcher( text );
        if( matcher.matches() ) {
            return parse.apply( matcher );
        } else {
            throw new InvalidValueException( text, valueType );
        }
    }
    
    static List< String > toList( String arrayText ) {
        arrayText = arrayText.strip();
        if( !( arrayText.startsWith( "[" ) && arrayText.endsWith( "]" ) ) ) {
            throw new ParseFailedException( arrayText + " does not match [ a, b, c,... ] format" );
        }
        arrayText = arrayText.substring( 1, arrayText.length()-1 ) + ',';
        var length = arrayText.length();
        var list = new ArrayList< String >();
        var startIndex = 0;
        var openedBracket = 0;
        for( var index = 0 ; index < length ; index++ ) {
            var c = arrayText.charAt( index );
            if( c == '[' || c == '{' ){
                openedBracket++;
            } else if( c == ']' || c == '}' ) {
                openedBracket--;
            } else if( openedBracket == 0 && c == ',' ) {
                list.add( arrayText.substring( startIndex, index ) );
                startIndex = index + 1;
            }
        }
        
        return list;
    }
}
