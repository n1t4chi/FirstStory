/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.dataParsers;

import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.input.Exceptions.ParseFailedException;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.object.Vertices;
import com.firststory.firstoracle.object2D.Object2D;
import com.firststory.firstoracle.object2D.Vertices2D;
import com.firststory.firstoracle.object3D.Object3D;
import com.firststory.firstoracle.object3D.Vertices3D;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.input.ParseUtils.METHOD_SET_VERTICES;
import static com.firststory.firstoracle.input.ParseUtils.toList;

/**
 * @author n1t4chi
 */
public interface VerticesParser {
    
    static void setVertices2D(
        Object2D< ?, ? > object,
        SharedData sharedData,
        String verticesText
    ) {
        setVertices(
            object,
            sharedData,
            SharedData::getVertices2D,
            Vertices2D.class,
            ParseUtils::toVec2,
            Position2D::pos2,
            Vertices2D::new,
            verticesText
        );
    }
    
    static void setVertices3D(
        Object3D< ?, ? > object,
        SharedData sharedData,
        String verticesText
    ) {
        setVertices(
            object,
            sharedData,
            SharedData::getVertices3D,
            Vertices3D.class,
            ParseUtils::toVec3,
            Position3D::pos3,
            Vertices3D::new,
            verticesText
        );
    }
    
    static Vertices2D newVertices2D( String text ) {
        return newVertices(
            ParseUtils::toVec2,
            Position2D::pos2,
            Vertices2D::new,
            text
        );
    }
    
    static Vertices3D newVertices3D( String text ) {
        return newVertices(
            ParseUtils::toVec3,
            Position3D::pos3,
            Vertices3D::new,
            text
        );
    }
    
    static < Vector, Posistion extends Position, VerticesT extends Vertices<?,?> > void setVertices(
        GraphicObject< ?, ?, ? extends VerticesT > object,
        SharedData sharedData,
        BiFunction< SharedData, String, VerticesT > sharedDataExtractor,
        Class< VerticesT > verticesClass,
        Function< String, Vector > toVector,
        Function< Vector, Posistion > toPosition,
        Function< List< Posistion >[] , VerticesT > verticesProvider,
        String verticesText
    ) {
        if( verticesText == null ) {
            return;
        }
        try {
            object.getClass()
                .getMethod( METHOD_SET_VERTICES, verticesClass )
                .invoke( object,
                    newVertices(
                        sharedData,
                        sharedDataExtractor,
                        toVector,
                        toPosition,
                        verticesProvider,
                        verticesText
                    )
                );
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
    }
    
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    static < Vector, PositionT extends Position, VerticesT extends Vertices<?,?> > VerticesT newVertices(
        SharedData sharedData,
        BiFunction< SharedData, String, VerticesT > sharedDataExtractor,
        Function< String, Vector > toVector,
        Function< Vector, PositionT > toPosition,
        Function< List< PositionT >[], VerticesT > verticesProvider,
        String verticesText
    ) {
        return ParseUtils.getNewOrShared(
            verticesText,
            sharedData,
            sharedDataExtractor,
            () -> newVertices( toVector, toPosition, verticesProvider, verticesText )
        );
    }
    
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    static < Vector, PositionT extends Position, VerticesT extends Vertices<?,?> > VerticesT newVertices(
        Function< String, Vector > toVector,
        Function< Vector, PositionT > toPosition,
        Function< List< PositionT >[], VerticesT > verticesProvider,
        String verticesText
    ) {
        return ( VerticesT ) verticesProvider.apply( toList( verticesText ).stream()
            .map( ParseUtils::toList )
            .map( verticesText1 -> verticesText1.stream()
                .map( toVector )
                .map( toPosition )
                .collect( Collectors.toList() )
            )
            .toArray( List[]::new )
        );
    }
}
