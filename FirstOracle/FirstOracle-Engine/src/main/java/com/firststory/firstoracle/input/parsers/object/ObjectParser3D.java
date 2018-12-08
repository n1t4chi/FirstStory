/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.input.*;
import com.firststory.firstoracle.input.parsers.ParameterParser;
import com.firststory.firstoracle.input.parsers.classes.*;
import com.firststory.firstoracle.object3D.*;

import java.util.List;

/**
 * @author n1t4chi
 */
public class ObjectParser3D extends ObjectParser<
    Position3D,
    Index3D,
    PositionableObject3D< ?, ? >,
    Terrain3D< ?, ? >,
    Object3DClassParser,
    Terrain3DClassParser
> {
    
    public Terrain3DClassParser getTerrainClassParser(
        SharedData sharedData
    ) {
        return sharedData.getTerrain3DClassParser();
    }
    
    public Object3DClassParser getObjectClassParser(
        SharedData sharedData
    ) {
        return sharedData.getObject3DClassParser();
    }
    
    @Override
    List< Index3D > parseIndices( String text ) {
        return ParseUtils.toIndices3D( text );
    }
    
    @Override
    Position3D parsePosition( String text ) {
        return ParseUtils.toPosition3D( text );
    }
    
    @Override
    String positionToString( Position3D position ) {
        return ParseUtils.fromPosition3D( position );
    }
    
    @Override
    Class< PositionableObject3DImpl > getDefaultObjectClass() {
        return PositionableObject3DImpl.class;
    }
    
    @Override
    Class< Terrain3DImpl > getDefaultTerrainClass() {
        return Terrain3DImpl.class;
    }
    
    @Override
    List< ParameterParser< ?, ? > > getSpecificObjectParsers( SharedData sharedData ) {
        return List.of(
            sharedData.getPosition3DParser(),
            sharedData.getPositionableTransformations3DParser()
        );
    }
    
    @Override
    List< ParameterParser< ?, ? > > getSpecificTerrainParsers( SharedData sharedData ) {
        return List.of(
            sharedData.getPosition3DCalculatorParser(),
            sharedData.getTransformations3DParser()
        );
    }
    
    @Override
    List< ParameterParser< ?, ? > > getSpecificCommonParsers( SharedData sharedData ) {
        return List.of(
            sharedData.getVertices3DParser(),
            sharedData.getRotation3DParser(),
            sharedData.getScale3DParser()
        );
    }
    
    @Override
    SharedObjectsParser getSharedTerrainsParser( SharedObjects sharedObjects ) {
        return sharedObjects.getSharedTerrains3DParser();
    }
    
    @Override
    SharedObjectsParser getSharedObjectsParser( SharedObjects sharedObjects ) {
        return sharedObjects.getSharedObjects3DParser();
    }
}
