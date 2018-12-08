/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.input.*;
import com.firststory.firstoracle.input.parsers.ParameterParser;
import com.firststory.firstoracle.input.parsers.classes.*;
import com.firststory.firstoracle.object2D.*;

import java.util.List;

/**
 * @author n1t4chi
 */
public class ObjectParser2D extends ObjectParser<
    Position2D,
    Index2D,
    PositionableObject2D< ?, ? >,
    Terrain2D< ?, ? >,
    Object2DClassParser,
    Terrain2DClassParser
> {
    
    public Terrain2DClassParser getTerrainClassParser( SharedData sharedData ) {
        return sharedData.getTerrain2DClassParser();
    }
    
    public Object2DClassParser getObjectClassParser( SharedData sharedData ) {
        return sharedData.getObject2DClassParser();
    }
    
    @Override
    List< Index2D > parseIndices( String text ) {
        return ParseUtils.toIndices2D( text );
    }
    
    @Override
    Position2D parsePosition( String text ) {
        return ParseUtils.toPosition2D( text );
    }
    
    @Override
    String positionToString( Position2D position ) {
        return ParseUtils.fromPosition2D( position );
    }
    
    @Override
    Class< PositionableObject2DImpl > getDefaultObjectClass() {
        return PositionableObject2DImpl.class;
    }
    
    @Override
    Class< Terrain2DImpl > getDefaultTerrainClass() {
        return Terrain2DImpl.class;
    }
    
    @Override
    List< ParameterParser< ?, ? > > getSpecificObjectParsers( SharedData sharedData ) {
        return List.of(
            sharedData.getPosition2DParser(),
            sharedData.getPositionableTransformations2DParser()
        );
    }
    
    @Override
    List< ParameterParser< ?, ? > > getSpecificTerrainParsers( SharedData sharedData ) {
        return List.of(
            sharedData.getPosition2DCalculatorParser(),
            sharedData.getTransformations2DParser()
        );
    }
    
    @Override
    List< ParameterParser< ?, ? > > getSpecificCommonParsers( SharedData sharedData ) {
        return List.of(
            sharedData.getVertices2DParser(),
            sharedData.getRotation2DParser(),
            sharedData.getScale2DParser()
        );
    }
    
    @Override
    SharedObjectsParser getSharedTerrainsParser( SharedObjects sharedObjects ) {
        return sharedObjects.getSharedTerrains2DParser();
    }
    
    @Override
    SharedObjectsParser getSharedObjectsParser( SharedObjects sharedObjects ) {
        return sharedObjects.getSharedObjects2DParser();
    }
}
