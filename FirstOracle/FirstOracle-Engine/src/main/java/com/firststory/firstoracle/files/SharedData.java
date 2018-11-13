/*
 * Copyright (c) 2018 Piotr PARAM_SHARED_ Olejarz
 */

package com.firststory.firstoracle.files;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.files.Exceptions.SharedDataKeyNotFoundException;
import com.firststory.firstoracle.files.dataParsers.*;
import com.firststory.firstoracle.files.structure.Composite;
import com.firststory.firstoracle.object.Colouring;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Vertices2D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Vertices3D;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.firststory.firstoracle.files.ParseUtils.*;

/**
 * @author n1t4chi
 */
public class SharedData {
    
    private final Map< String, Class< ? extends PositionableObject2D< ?, ? > > > classes2D = new HashMap<>();
    private final Map< String, Position2D > positions2D = new HashMap<>();
    private final Map< String, Rotation2D > rotations2D = new HashMap<>();
    private final Map< String, Scale2D > scales2D = new HashMap<>();
    private final Map< String, Vertices2D > vertices2D = new HashMap<>();
    
    private final Map< String, Class< ? extends PositionableObject3D< ?, ? > > > classes3D = new HashMap<>();
    private final Map< String, Position3D > positions3D = new HashMap<>();
    private final Map< String, Rotation3D > rotations3D = new HashMap<>();
    private final Map< String, Scale3D > scales3D = new HashMap<>();
    private final Map< String, Vertices3D > vertices3D = new HashMap<>();
    
    private final Map< String, Texture > textures = new HashMap<>();
    private final Map< String, UvMap > uvMaps = new HashMap<>();
    private final Map< String, Colouring > colourings = new HashMap<>();
    
    public SharedData( Composite sharedDataNode ) {
        parseClasses( sharedDataNode );
        parsePositions( sharedDataNode );
        parseRotations( sharedDataNode );
        parseScales( sharedDataNode );
        parseVertices( sharedDataNode );
        parseTextures( sharedDataNode );
        parseUvMaps( sharedDataNode );
        parseColourings( sharedDataNode );
    
    }
    
    private void parseTextures( Composite sharedDataNode ) {
        parse( sharedDataNode,
            PARAM_SHARED_TEXTURES,
            this::addTexture,
            TextureParser::newTexture
        );
    }
    
    private void parseVertices( Composite sharedDataNode ) {
        parse( sharedDataNode,
            PARAM_SHARED_VERTICES_2D,
            this::addVertices2D,
            VerticesParser::newVertices2D
        );
        parse( sharedDataNode,
            PARAM_SHARED_VERTICES_3D,
            this::addVertices3D,
            VerticesParser::newVertices3D
        );
    }
    
    private void parseUvMaps( Composite sharedDataNode ) {
        parse( sharedDataNode,
            PARAM_SHARED_UV_MAPS,
            this::addUvMap,
            UvMapParser::newUvMap
        );
    }
    
    private void parseColourings( Composite sharedDataNode ) {
        parse( sharedDataNode,
            PARAM_SHARED_COLOURINGS,
            this::addColouring,
            ColouringParser::newColouring
        );
    }
    
    private void parseScales( Composite sharedDataNode ) {
        parse( sharedDataNode,
            PARAM_SHARED_SCALES_2D,
            this::addScale2D,
            ParseUtils::toScale2D
        );
        parse( sharedDataNode,
            PARAM_SHARED_SCALES_3D,
            this::addScale3D,
            ParseUtils::toScale3D
        );
    }
    
    private void parseRotations( Composite sharedDataNode ) {
        parse( sharedDataNode,
            PARAM_SHARED_ROTATIONS_2D,
            this::addRotation2D,
            ParseUtils::toRotation2D
        );
        parse( sharedDataNode,
            PARAM_SHARED_ROTATIONS_3D,
            this::addRotation3D,
            ParseUtils::toRotation3D
        );
    }
    
    private void parsePositions( Composite sharedDataNode ) {
        parse( sharedDataNode,
            PARAM_SHARED_POSITIONS_2D,
            this::addPosition2D,
            ParseUtils::toPosition2D
        );
        parse( sharedDataNode,
            PARAM_SHARED_POSITIONS_3D,
            this::addPosition3D,
            ParseUtils::toPosition3D
        );
    }
    
    private void parseClasses( Composite sharedDataNode ) {
        parse( sharedDataNode,
            PARAM_SHARED_CLASSES_2D,
            this::addClass2D,
            ClassParser::getNewObjectClass2D
        );
        parse( sharedDataNode,
            PARAM_SHARED_CLASSES_3D,
            this::addClass3D,
            ClassParser::getNewObjectClass3D
        );
    }
    
    private <T> void parse(
        Composite sharedDataNode,
        String sharedName,
        BiConsumer< String, T > adder,
        Function< String, T > parser
    ) {
        var values = sharedDataNode.findComposite( sharedName );
        values.getLeafs().forEach( leaf -> adder.accept(
            leaf.getName(),
            parser.apply( leaf.getValue() )
        ) );
    }
    
    public SharedData() {}
    
    public Class< ? extends PositionableObject2D< ?, ? > > getClass2D( String key ) {
        return get( classes2D, key, PARAM_SHARED_CLASSES_2D );
    }
    
    public Position2D getPosition2D( String key ) {
        return get( positions2D, key, PARAM_SHARED_POSITIONS_2D );
    }
    
    public Rotation2D getRotation2D( String key ) {
        return get( rotations2D, key, PARAM_SHARED_ROTATIONS_2D );
    }
    
    public Scale2D getScale2D( String key ) {
        return get( scales2D, key, PARAM_SHARED_SCALES_2D );
    }
    
    public Vertices2D getVertices2D( String key ) {
        return get( vertices2D, key, PARAM_SHARED_VERTICES_2D );
    }
    
    public Class< ? extends PositionableObject3D< ?, ? > > getClass3D( String key ) {
        return get( classes3D, key, PARAM_SHARED_CLASSES_3D );
    }
    
    public Position3D getPosition3D( String key ) {
        return get( positions3D, key, PARAM_SHARED_POSITIONS_3D );
    }
    
    public Rotation3D getRotation3D( String key ) {
        return get( rotations3D, key, PARAM_SHARED_ROTATIONS_3D );
    }
    
    public Scale3D getScale3D( String key ) {
        return get( scales3D, key, PARAM_SHARED_SCALES_3D );
    }
    
    public Vertices3D getVertices3D( String key ) {
        return get( vertices3D, key, PARAM_SHARED_VERTICES_3D );
    }
    
    public Texture getTexture( String key ) {
        return get( textures, key, PARAM_SHARED_TEXTURES );
    }
    
    public UvMap getUvMap( String key ) {
        return get( uvMaps, key, PARAM_SHARED_UV_MAPS );
    }
    
    public Colouring getColouring( String key ) {
        return get( colourings, key, PARAM_SHARED_COLOURINGS );
    }
    
    public void addClass2D( String key, Class< ? extends PositionableObject2D< ?, ? > > value ) {
        classes2D.put( key, value );
    }
    
    public void addPosition2D( String key, Position2D value ) {
        positions2D.put( key, value );
    }
    
    public void addRotation2D( String key, Rotation2D value ) {
        rotations2D.put( key, value );
    }
    
    public void addScale2D( String key, Scale2D value ) {
        scales2D.put( key, value );
    }
    
    public void addVertices2D( String key, Vertices2D value ) {
        vertices2D.put( key, value );
    }
    
    public void addClass3D( String key, Class< ? extends PositionableObject3D< ?, ? > > value ) {
        classes3D.put( key, value );
    }
    
    public void addPosition3D( String key, Position3D value ) {
        positions3D.put( key, value );
    }
    
    public void addRotation3D( String key, Rotation3D value ) {
        rotations3D.put( key, value );
    }
    
    public void addScale3D( String key, Scale3D value ) {
        scales3D.put( key, value );
    }
    
    public void addVertices3D( String key, Vertices3D value ) {
        vertices3D.put( key, value );
    }
    
    public void addTexture( String key, Texture value ) {
        textures.put( key, value );
    }
    
    public void addUvMap( String key, UvMap value ) {
        uvMaps.put( key, value );
    }
    
    public void addColouring( String key, Colouring value ) {
        colourings.put( key, value );
    }
    
    private <T> T get( Map<String, T> map, String key, String typeName ){
        var value = map.get( key );
        if( value == null ) {
            throw new SharedDataKeyNotFoundException( key, typeName );
        }
        return value;
    }
}
