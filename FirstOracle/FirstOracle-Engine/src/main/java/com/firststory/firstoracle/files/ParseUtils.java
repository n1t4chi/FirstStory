/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.files.Exceptions.InvalidValueException;
import com.firststory.firstoracle.files.Exceptions.ParseFailedException;
import org.joml.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author n1t4chi
 */
public interface ParseUtils {
    
    String NAME_SCENE_2D = "scene2D";
    String NAME_SCENE_3D = "scene3D";
    String NAME_OBJECTS = "objects";
    String NAME_TERRAINS = "terrains";
    
    String PARAM_OBJECT_CLASS = "class";
    String PARAM_OBJECT_POSITION = "position";
    String PARAM_OBJECT_ROTATION = "rotation";
    String PARAM_OBJECT_SCALE = "scale";
    String PARAM_OBJECT_TEXTURE = "texture";
    String PARAM_OBJECT_UV_MAP = "uvMap";
    String PARAM_OBJECT_VERTICES = "vertices";
    String PARAM_OBJECT_COLOURING = "colouring";
    
    String PARAM_SHARED_DATA = "sharedData";
    
    String PARAM_SHARED_CLASSES_2D = "classes2D";
    String PARAM_SHARED_POSITIONS_2D = "positions2D";
    String PARAM_SHARED_ROTATIONS_2D = "rotations2D";
    String PARAM_SHARED_SCALES_2D = "scales2D";
    String PARAM_SHARED_VERTICES_2D = "vertices2D";
    
    String PARAM_SHARED_CLASSES_3D = "classes3D";
    String PARAM_SHARED_POSITIONS_3D = "positions3D";
    String PARAM_SHARED_ROTATIONS_3D = "rotations3D";
    String PARAM_SHARED_SCALES_3D = "scales3D";
    String PARAM_SHARED_VERTICES_3D = "vertices3D";
    
    String PARAM_SHARED_TEXTURES = "textures";
    String PARAM_SHARED_UV_MAPS = "uvMaps";
    String PARAM_SHARED_COLOURINGS = "colourings";
    
    String SHARED_NAME_PREFIX = "$";
    
    String PARAM_SHARED_OBJECTS = "sharedObjects";
    String PARAM_SHARED_TERRAINS_3D = "Objects3D";
    String PARAM_SHARED_TERRAINS_2D = "Objects2D";
    String PARAM_SHARED_OBJECTS_3D = "Terrains3D";
    String PARAM_SHARED_OBJECTS_2D = "Terrains2D";
    
    String METHOD_SET_TEXTURE = "setTexture";
    String METHOD_SET_COLOURING = "setColouring";
    String METHOD_SET_UV_MAP = "setUvMap";
    String METHOD_SET_VERTICES = "setVertices";
    
    
    String FLOAT = "(\\d+(\\.\\d+)?)";
    String DIV = ",";
    Pattern PatternVec4 = Pattern.compile( FLOAT + DIV + FLOAT + DIV + FLOAT + DIV + FLOAT );
    Pattern PatternVec3 = Pattern.compile( FLOAT + DIV + FLOAT + DIV + FLOAT );
    Pattern PatternVec2 = Pattern.compile( FLOAT + DIV + FLOAT );
    Pattern PatternVec1 = Pattern.compile( FLOAT );
    
    static boolean isShared( String name ) {
        return name.startsWith( SHARED_NAME_PREFIX );
    }
    
    static < T > T getNewOrShared(
        String name,
        SharedData sharedData,
        BiFunction< SharedData, String, T > sharedDataExtractor,
        Function< String, T > normalSupplier
    ) {
        return getNewOrShared(
            name,
            sharedData,
            sharedDataExtractor,
            () -> normalSupplier.apply( name )
        );
    }
    
    static < T > T getNewOrShared(
        String name,
        SharedData sharedData,
        BiFunction< SharedData, String, T > sharedDataExtractor,
        Supplier< T > normalSupplier
    ) {
        if( isShared( name ) ) {
            return sharedDataExtractor.apply( sharedData, name.substring( 1 ) );
        } else {
            return normalSupplier.get();
        }
    }
    
    static String indent( String node ) {
        return node.replaceAll( "\n", "\n\t" );
    }
    
    static Position2D toPosition2D( String text ) {
        return Position2D.pos2( toVec2( text ) );
    }
    
    static Position3D toPosition3D( String text ) {
        return Position3D.pos3( toVec3( text ) );
    }
    
    static Scale2D toScale2D( String text ) {
        return Scale2D.scale2( toVec2( text ) );
    }
    
    static Scale3D toScale3D( String text ) {
        return Scale3D.scale3( toVec3( text ) );
    }
    
    static Rotation2D toRotation2D( String text ) {
        return Rotation2D.rot2( toVec1( text ) );
    }
    
    static Rotation3D toRotation3D( String text ) {
        return Rotation3D.rot3( toVec3( text ) );
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
    
    interface TriConsumer< A, B, C > {
        void accept( A a, B b, C c );
    }
    
    interface QuadConsumer< A, B, C, D > {
        void accept( A a, B b, C c, D d );
    }
    
    interface PentaConsumer< A, B, C, D, E > {
        void accept( A a, B b, C c, D d, E e );
    }
}
