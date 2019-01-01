/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.input.parsers.classes.*;
import com.firststory.firstoracle.input.parsers.object.*;
import com.firststory.firstoracle.input.structure.*;
import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.scene.*;

import java.util.Collection;
import java.util.function.BiConsumer;

import static com.firststory.firstoracle.input.ParseUtils.*;

/**
 * @author n1t4chi
 */
public class SceneParser {
    
    private static final ObjectParser2D PARSER_2D = new ObjectParser2D();
    private static final ObjectParser3D PARSER_3D = new ObjectParser3D();
    
    public static OptimisedRegistrableScene2DImpl parseToOptimised2D( String text ) {
        var scene2D = new OptimisedRegistrableScene2DImpl();
        new SceneParser( text ).parseScene2D( text, scene2D );
        return scene2D;
    }
    
    public static OptimisedRegistrableScene3DImpl parseToOptimised3D( String text ) {
        var scene3D = new OptimisedRegistrableScene3DImpl();
        new SceneParser( text ).parseScene3D( text, scene3D );
        return scene3D;
    }
    
    public static RegistrableScene2DImpl parseToNonOptimised2D( String text ) {
        var scene2D = new RegistrableScene2DImpl();
        new SceneParser( text ).parseScene2D( text, scene2D );
        return scene2D;
    }
    
    public static RegistrableScene3DImpl parseToNonOptimised3D( String text ) {
        
        var scene3D = new RegistrableScene3DImpl();
        new SceneParser( text ).parseScene3D( text, scene3D );
        return scene3D;
    }
    
    private final Roots configRoot;
    private final SceneConfiguration configuration;
    private final SharedData sharedData;
    private final SharedObjects sharedObjects;
    
    public SceneConfiguration getConfiguration() {
        return configuration;
    }
    
    public SharedData getSharedData() {
        return sharedData;
    }
    
    public SharedObjects getSharedObjects() {
        return sharedObjects;
    }
    
    public SceneParser( String dataText ) {
        configRoot = Roots.parse( dataText );
        configuration = new SceneConfiguration( configRoot.find( CONFIGURATION ) );
        sharedData = new SharedData( configRoot.find( SHARED_PARAM ) );
        sharedObjects = new SharedObjects( configRoot.find( SHARED_OBJECTS ) );
    }
    
    
    public void parseScene2D( String sceneText, RegistrableScene2D scene2D ) {
        var sceneRoot = Roots.parse( sceneText );
        parseScene2D( sceneRoot, scene2D );
    }
    
    public void parseScene3D( String sceneText, RegistrableScene3D scene3D ) {
        var sceneRoot = Roots.parse( sceneText );
        parseScene3D( sceneRoot, scene3D );
    }
    
    public PositionableObject3D< ?, ? > createPositionableObject3DFromShared( String objectName ) {
    
        MutableComposite objectNode = new MutableComposite( "parsed" );
        objectNode.addContent( new Leaf( "base", SHARED_NAME_PREFIX+objectName ) );
        var parsedObjectNode = sharedObjects.getSharedObjects3DParser().parseNode( objectNode );
        return PARSER_3D.toObject( sharedData, sharedObjects, parsedObjectNode );
    }
    
    private void parseScene2D(
        Roots sceneRoots,
        RegistrableScene2D scene2D
    ) {
        parseScene(
            sceneRoots.find( SCENE_2D ),
            scene2D,
            PARSER_2D,
            RegistrableScene2D::registerMultipleObjects2D,
            RegistrableScene2D::registerMultipleTerrains2D
        );
    }
    
    private void parseScene3D(
        Roots roots,
        RegistrableScene3D scene3D
    ) {
        parseScene(
            roots.find( SCENE_3D ),
            scene3D,
            PARSER_3D,
            RegistrableScene3D::registerMultipleObjects3D,
            RegistrableScene3D::registerMultipleTerrains3D
        );
    }
    
    private <
        Scene,
        IndexType extends Index,
        PositionType extends Position,
        PositionableObjectType extends PositionableObject< PositionType, ?, ?, ?, ?, ? >,
        TerrainType extends Terrain< PositionType, ?, ?, ?, ?, ?, IndexType, ? >,
        ObjectClassParserType extends ObjectClassParser< PositionableObjectType >,
        TerrainClassParserType extends TerrainClassParser< TerrainType >
    > void parseScene(
        Composite sceneNode,
        Scene scene,
        ObjectParser<
            PositionType,
            IndexType,
            PositionableObjectType,
            TerrainType,
            ObjectClassParserType,
            TerrainClassParserType
        > parser,
        BiConsumer< Scene, Collection< PositionableObjectType > > registerObject,
        TriConsumer< Scene, TerrainType, Collection< IndexType >  > registerTerrain
    ) {
        var objects = parser.getObjects( sharedData, sharedObjects, sceneNode );
        var terrains = parser.getTerrains( sharedData, sharedObjects, sceneNode );
    
        terrains.forEach( ( name, pair ) ->
            registerTerrain.accept( scene, pair.getTerrain(), pair.getIndices() )
        );
        objects.forEach( ( name, list ) -> {
            registerObject.accept( scene, list );
        } );
    }
}