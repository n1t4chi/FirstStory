/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.input.Exceptions.SharedDataKeyNotFoundException;
import com.firststory.firstoracle.input.parameters.*;
import com.firststory.firstoracle.input.structure.Composite;
import com.firststory.firstoracle.object.Colouring;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object2D.Position2DCalculator;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object2D.Vertices2D;
import com.firststory.firstoracle.object3D.Position3DCalculator;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.object3D.Vertices3D;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.firststory.firstoracle.input.ParseUtils.*;

/**
 * @author n1t4chi
 */
public class SharedData {
    
    private final Map< String, Class< ? extends PositionableObject2D< ?, ? > > > objectClasses2D = new HashMap<>();
    private final Map< String, Class< ? extends Terrain2D< ? > > > terrainClasses2D = new HashMap<>();
    private final Map< String, Position2D > positions2D = new HashMap<>();
    private final Map< String, Rotation2D > rotations2D = new HashMap<>();
    private final Map< String, Scale2D > scales2D = new HashMap<>();
    private final Map< String, Vertices2D > vertices2D = new HashMap<>();
    private final Map< String, Position2DCalculator > position2DCalculators = new HashMap<>();
    
    private final Map< String, Class< ? extends PositionableObject3D< ?, ? > > > objectClasses3D = new HashMap<>();
    private final Map< String, Class< ? extends Terrain3D< ? > > > terrainClasses3D = new HashMap<>();
    private final Map< String, Position3D > positions3D = new HashMap<>();
    private final Map< String, Rotation3D > rotations3D = new HashMap<>();
    private final Map< String, Scale3D > scales3D = new HashMap<>();
    private final Map< String, Vertices3D > vertices3D = new HashMap<>();
    private final Map< String, Position3DCalculator > position3DCalculators = new HashMap<>();
    
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
        parsePositionCalculators( sharedDataNode );
    }
    
    private void parsePositionCalculators( Composite sharedDataNode ) {
        parse( sharedDataNode,
            SHARED_PARAM_POSITION_CALCULATORS_2D,
            this::addPosition2DCalculator,
            PositionCalculatorParser::newCalculator2D
        );
        parse( sharedDataNode,
            SHARED_PARAM_POSITION_CALCULATORS_3D,
            this::addPosition3DCalculator,
            PositionCalculatorParser::newCalculator3D
        );
    }
    
    private void parseTextures( Composite sharedDataNode ) {
        parse( sharedDataNode,
            SHARED_PARAM_TEXTURES,
            this::addTexture,
            TextureParser::newTexture
        );
    }
    
    private void parseVertices( Composite sharedDataNode ) {
        parse( sharedDataNode,
            SHARED_PARAM_VERTICES_2D,
            this::addVertices2D,
            VerticesParser::newVertices2D
        );
        parse( sharedDataNode,
            SHARED_PARAM_VERTICES_3D,
            this::addVertices3D,
            VerticesParser::newVertices3D
        );
    }
    
    private void parseUvMaps( Composite sharedDataNode ) {
        parse( sharedDataNode,
            SHARED_PARAM_UV_MAPS,
            this::addUvMap,
            UvMapParser::newUvMap
        );
    }
    
    private void parseColourings( Composite sharedDataNode ) {
        parse( sharedDataNode,
            SHARED_PARAM_COLOURINGS,
            this::addColouring,
            ColouringParser::newColouring
        );
    }
    
    private void parseScales( Composite sharedDataNode ) {
        parse( sharedDataNode,
            SHARED_PARAM_SCALES_2D,
            this::addScale2D,
            ParseUtils::toScale2D
        );
        parse( sharedDataNode,
            SHARED_PARAM_SCALES_3D,
            this::addScale3D,
            ParseUtils::toScale3D
        );
    }
    
    private void parseRotations( Composite sharedDataNode ) {
        parse( sharedDataNode,
            SHARED_PARAM_ROTATIONS_2D,
            this::addRotation2D,
            ParseUtils::toRotation2D
        );
        parse( sharedDataNode,
            SHARED_PARAM_ROTATIONS_3D,
            this::addRotation3D,
            ParseUtils::toRotation3D
        );
    }
    
    private void parsePositions( Composite sharedDataNode ) {
        parse( sharedDataNode,
            SHARED_PARAM_POSITIONS_2D,
            this::addPosition2D,
            ParseUtils::toPosition2D
        );
        parse( sharedDataNode,
            SHARED_PARAM_POSITIONS_3D,
            this::addPosition3D,
            ParseUtils::toPosition3D
        );
    }
    
    private void parseClasses( Composite sharedDataNode ) {
        parse( sharedDataNode,
            SHARED_PARAM_OBJECT_CLASSES_2D,
            this::addObjectClass2D,
            ClassParser::getNewObjectClass2D
        );
        parse( sharedDataNode,
            SHARED_PARAM_OBJECT_CLASSES_3D,
            this::addObjectClass3D,
            ClassParser::getNewObjectClass3D
        );
        parse( sharedDataNode,
            SHARED_PARAM_TERRAIN_CLASSES_2D,
            this::addTerrainClass2D,
            ClassParser::getNewTerrainClass2D
        );
        parse( sharedDataNode,
            SHARED_PARAM_TERRAIN_CLASSES_3D,
            this::addTerrainClass3D,
            ClassParser::getNewTerrainClass3D
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
    
    public Class< ? extends PositionableObject2D< ?, ? > > getObjectClass2D( String key ) {
        return get( objectClasses2D, key,
            SHARED_PARAM_OBJECT_CLASSES_2D
        );
    }
    
    public Class< ? extends Terrain2D< ? > > getTerrainClass2D( String key ) {
        return get( terrainClasses2D, key,
            SHARED_PARAM_TERRAIN_CLASSES_2D
        );
    }
    
    public Position2D getPosition2D( String key ) {
        return get( positions2D, key,
            SHARED_PARAM_POSITIONS_2D
        );
    }
    
    public Rotation2D getRotation2D( String key ) {
        return get( rotations2D, key, SHARED_PARAM_ROTATIONS_2D );
    }
    
    public Scale2D getScale2D( String key ) {
        return get( scales2D, key, SHARED_PARAM_SCALES_2D );
    }
    
    public Vertices2D getVertices2D( String key ) {
        return get( vertices2D, key, SHARED_PARAM_VERTICES_2D );
    }
    
    public Class< ? extends PositionableObject3D< ?, ? > > getObjectClass3D( String key ) {
        return get( objectClasses3D, key,
            SHARED_PARAM_OBJECT_CLASSES_3D
        );
    }
    
    public Class< ? extends Terrain3D< ? > > getTerrainClass3D( String key ) {
        return get( terrainClasses3D, key,
            SHARED_PARAM_TERRAIN_CLASSES_3D
        );
    }
    
    public Position3D getPosition3D( String key ) {
        return get( positions3D, key, SHARED_PARAM_POSITIONS_3D );
    }
    
    public Rotation3D getRotation3D( String key ) {
        return get( rotations3D, key, SHARED_PARAM_ROTATIONS_3D );
    }
    
    public Scale3D getScale3D( String key ) {
        return get( scales3D, key, SHARED_PARAM_SCALES_3D );
    }
    
    public Vertices3D getVertices3D( String key ) {
        return get( vertices3D, key, SHARED_PARAM_VERTICES_3D );
    }
    
    public Texture getTexture( String key ) {
        return get( textures, key, SHARED_PARAM_TEXTURES );
    }
    
    public UvMap getUvMap( String key ) {
        return get( uvMaps, key, SHARED_PARAM_UV_MAPS );
    }
    
    public Colouring getColouring( String key ) {
        return get( colourings, key, SHARED_PARAM_COLOURINGS );
    }
    
    public Position2DCalculator getPosition2DCalculator( String key ) {
        return get( position2DCalculators, key, SHARED_PARAM_POSITION_CALCULATORS_2D );
    }
    
    public Position3DCalculator getPosition3DCalculator( String key ) {
        return get( position3DCalculators, key, SHARED_PARAM_POSITION_CALCULATORS_3D );
    }
    
    public void addObjectClass2D( String key, Class< ? extends PositionableObject2D< ?, ? > > value ) {
        objectClasses2D.put( key, value );
    }
    
    public void addTerrainClass2D( String key, Class< ? extends Terrain2D< ? > > value ) {
        terrainClasses2D.put( key, value );
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
    
    public void addObjectClass3D( String key, Class< ? extends PositionableObject3D< ?, ? > > value ) {
        objectClasses3D.put( key, value );
    }
    
    public void addTerrainClass3D( String key, Class< ? extends Terrain3D< ? > > value ) {
        terrainClasses3D.put( key, value );
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
    
    public void addPosition2DCalculator( String key, Position2DCalculator value ) {
        position2DCalculators.put( key, value );
    }
    public void addPosition3DCalculator( String key, Position3DCalculator value ) {
        position3DCalculators.put( key, value );
    }
    
    private <T> T get( Map<String, T> map, String key, String typeName ){
        var value = map.get( key );
        if( value == null ) {
            throw new SharedDataKeyNotFoundException( key, typeName );
        }
        return value;
    }
}
