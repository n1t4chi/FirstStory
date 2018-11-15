/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.input.Exceptions.InvalidValueException;
import com.firststory.firstoracle.input.Exceptions.ParseFailedException;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author n1t4chi
 */
public interface ParseUtils {
    
    String SCENE_2D = "scene2D";
    String SCENE_3D = "scene3D";
    String SCENE_OBJECTS = "objects";
    String SCENE_TERRAINS = "terrains";
    
    String SCENE_PARAM_CLASS = "class";
    String SCENE_PARAM_POSITION = "position";
    String SCENE_PARAM_ROTATION = "rotation";
    String SCENE_PARAM_SCALE = "scale";
    String SCENE_PARAM_TEXTURE = "texture";
    String SCENE_PARAM_UV_MAP = "uvMap";
    String SCENE_PARAM_VERTICES = "vertices";
    String SCENE_PARAM_COLOURING = "colouring";
    String SCENE_PARAM_INDICES = "indices";
    String SCENE_PARAM_POSITION_CALC = "positionCalculator";
    
    String METHOD_SET_TEXTURE = "setTexture";
    String METHOD_SET_COLOURING = "setColouring";
    String METHOD_SET_UV_MAP = "setUvMap";
    String METHOD_SET_VERTICES = "setVertices";
    String METHOD_SET_POSITION_CALCULATOR = "setPositionCalculator";
    
    
    String SHARED_NAME_PREFIX = "$";
    String SHARED_OBJECTS = "sharedObjects";
    String SHARED_OBJECTS_TERRAINS_3D = "objects3D";
    String SHARED_OBJECTS_TERRAINS_2D = "objects2D";
    String SHARED_OBJECTS_OBJECTS_3D = "terrains3D";
    String SHARED_OBJECTS_OBJECTS_2D = "terrains2D";
    
    String SHARED_PARAM = "sharedData";
    
    String SHARED_PARAM_OBJECT_CLASSES_2D = "objectClasses2D";
    String SHARED_PARAM_TERRAIN_CLASSES_2D = "terrainClasses2D";
    String SHARED_PARAM_POSITIONS_2D = "positions2D";
    String SHARED_PARAM_ROTATIONS_2D = "rotations2D";
    String SHARED_PARAM_SCALES_2D = "scales2D";
    String SHARED_PARAM_VERTICES_2D = "vertices2D";
    String SHARED_PARAM_POSITION_CALCULATORS_2D = "positionCalculators2D";
    
    String SHARED_PARAM_OBJECT_CLASSES_3D = "objectClasses3D";
    String SHARED_PARAM_TERRAIN_CLASSES_3D = "terrainClasses3D";
    String SHARED_PARAM_POSITIONS_3D = "positions3D";
    String SHARED_PARAM_ROTATIONS_3D = "rotations3D";
    String SHARED_PARAM_SCALES_3D = "scales3D";
    String SHARED_PARAM_VERTICES_3D = "vertices3D";
    String SHARED_PARAM_POSITION_CALCULATORS_3D = "positionCalculators3D";
    
    String SHARED_PARAM_TEXTURES = "textures";
    String SHARED_PARAM_UV_MAPS = "uvMaps";
    String SHARED_PARAM_COLOURINGS = "colourings";
    
    
    String CONFIGURATION = "configuration";
    String CONFIGURATION_TERRAIN_SHIFT_2D = "terrain2DShift";
    String CONFIGURATION_TERRAIN_SHIFT_3D = "terrain3DShift";
    String CONFIGURATION_TERRAIN_SIZE_2D = "terrain2DSize";
    String CONFIGURATION_TERRAIN_SIZE_3D = "terrain3DSize";
    
    String INT = "(\\d+)";
    String FLOAT = "(\\d+(\\.\\d+)?)";
    String DIV = ",";
    Pattern PatternVec4 = Pattern.compile( FLOAT + DIV + FLOAT + DIV + FLOAT + DIV + FLOAT );
    Pattern PatternVec3 = Pattern.compile( FLOAT + DIV + FLOAT + DIV + FLOAT );
    Pattern PatternVec2 = Pattern.compile( FLOAT + DIV + FLOAT );
    Pattern PatternVec1 = Pattern.compile( FLOAT );
    
    String PATTERN_2I = "\\{?" + INT + DIV + INT + "}?";
    String PATTERN_3I = "\\{?" + INT + DIV + INT + DIV + INT + "}?";
    Pattern PatternVeci2 = Pattern.compile( PATTERN_2I );
    Pattern PatternVeci3 = Pattern.compile( PATTERN_3I );
    String RANGE_COUPLER = "x";
    Pattern PatternRangeVeci2 = Pattern.compile( PATTERN_2I + RANGE_COUPLER + PATTERN_2I );
    Pattern PatternRangeVeci3 = Pattern.compile( PATTERN_3I + RANGE_COUPLER + PATTERN_3I );
    
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
    
    static List< Index2D > toIndices2D( String text ) {
        return toIndices(
            text,
            PatternVeci2,
            PatternRangeVeci2,
            ParseUtils::toIndex2D,
            ParseUtils::range2DToList
        );
    }
    
    static List<Index2D> range2DToList(
        Index2D start,
        Index2D end
    ) {
        return rangeToList(
            start,
            end,
            Index2D::leftFits,
            ( s,e ) -> IntStream.rangeClosed( s.x(), e.x() )
                .mapToObj( x ->
                    IntStream.rangeClosed( s.y(), e.y() )
                        .mapToObj( y -> Index2D.id2( x, y ) )
                        .collect( Collectors.toList() )
                ).flatMap( Collection::stream )
        );
    }
    
    static List< Index3D > toIndices3D( String text ) {
        return toIndices(
            text,
            PatternVeci3,
            PatternRangeVeci3,
            ParseUtils::toIndex3D,
            ParseUtils::range3DToList
        );
    }
    
    static List<Index3D> range3DToList(
        Index3D start,
        Index3D end
    ) {
        return rangeToList(
            start,
            end,
            Index3D::leftFits,
            ( s,e ) -> IntStream.rangeClosed( s.x(), e.x() )
                .mapToObj( x ->
                    IntStream.rangeClosed( s.y(), e.y() )
                        .mapToObj( y ->
                            IntStream.rangeClosed( s.z(), e.z() )
                                .mapToObj( z -> Index3D.id3( x, y, z ) )
                                .collect( Collectors.toList() )
                        ).flatMap( Collection::stream )
                        .collect( Collectors.toList() )
                ).flatMap( Collection::stream )
        );
    }
    
    static < IndexType extends Index > List< IndexType > toIndices(
        String text,
        Pattern singlePattern,
        Pattern rangePattern,
        Function< String, IndexType > indexParser,
        BiFunction< IndexType, IndexType, List< IndexType > > rangeToList
    ) {
        text = normalizeVectorText( text );
        var singleMatcher = singlePattern.matcher( text );
        if( singleMatcher.matches() ) {
            return List.of( indexParser.apply( text ) );
        }
        var rangeMatcher = rangePattern.matcher( text );
        if( rangeMatcher.matches() ) {
            var split = text.split( RANGE_COUPLER );
            if( split.length != 2 ) {
                throw new ParseFailedException( "Cannot split index range " + text + " into two indices" );
            }
            return rangeToList.apply(
                indexParser.apply( split[0] ),
                indexParser.apply( split[1] )
            );
        }
        throw new ParseFailedException( "Cannot parse " + text + " into neither index nor index range." );
    }
    
    static < IndexType extends Index > List< IndexType > rangeToList(
        IndexType start,
        IndexType end,
        BiPredicate< IndexType, IndexType > isRangeValid,
        BiFunction< IndexType, IndexType, Stream< IndexType > > rangeToStream
    ) {
        if( !isRangeValid.test( start, end ) ) {
            throw new ParseFailedException( start + "x" + end + " forms invalid negative range " );
        }
        return rangeToStream.apply( start, end )
            .collect( Collectors.toList() )
            ;
    }
    
    static Index2D toIndex2D( String text ) {
        return Index2D.id2( toVec2i( text ) );
    }
    
    static Index3D toIndex3D( String text ) {
        return Index3D.id3( toVec3i( text ) );
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
    
    static Vector2ic toVec2i( String text ) {
        return toVec(
            text,
            PatternVeci2,
            matcher -> new Vector2i(
                Integer.parseInt( matcher.group( 1 ) ),
                Integer.parseInt( matcher.group( 2 ) )
            ),
            "Index 2D"
        );
    }
    
    static Vector3ic toVec3i( String text ) {
        return toVec(
            text,
            PatternVeci3,
            matcher -> new Vector3i(
                Integer.parseInt( matcher.group( 1 ) ),
                Integer.parseInt( matcher.group( 2 ) ),
                Integer.parseInt( matcher.group( 3 ) )
            ),
            "Index 2D"
        );
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
        text = normalizeVectorText( text );
        var matcher = pattern.matcher( text );
        if( matcher.matches() ) {
            return parse.apply( matcher );
        } else {
            throw new InvalidValueException( text, valueType );
        }
    }
    
    @NotNull
    static String normalizeVectorText( String text ) {
        return text.replaceAll( "[{} ]", "" );
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
    
    interface TriFunction< A, B, C, R > {
        R apply( A a, B b, C c );
    }
    
    interface QuadFunction< A, B, C, D, R > {
        R apply( A a, B b, C c, D d );
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
