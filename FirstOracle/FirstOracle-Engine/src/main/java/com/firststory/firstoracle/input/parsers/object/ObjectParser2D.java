/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.SharedObjects;
import com.firststory.firstoracle.input.parsers.ParameterParser;
import com.firststory.firstoracle.input.parsers.classes.Object2DClassParser;
import com.firststory.firstoracle.input.parsers.classes.Terrain2DClassParser;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.PositionableObject2DImpl;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object2D.Terrain2DImpl;

import java.util.List;

/**
 * @author n1t4chi
 */
public class ObjectParser2D extends ObjectParser<
    PositionableObject2D< ?, ? >,
    Terrain2D< ? >,
    Index2D,
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
    List< Index2D > parseIndex( String text ) {
        return ParseUtils.toIndices2D( text );
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
    List< ParameterParser< ? > > getSpecificObjectParsers( SharedData sharedData ) {
        return List.of(
            sharedData.getPosition2DParser(),
            sharedData.getPositionableTransformations2DParser()
        );
    }
    
    @Override
    List< ParameterParser< ? > > getSpecificTerrainParsers( SharedData sharedData ) {
        return List.of(
            sharedData.getPosition2DCalculatorParser(),
            sharedData.getTransformations2DParser()
        );
    }
    
    @Override
    List< ParameterParser< ? > > getSpecificCommonParsers( SharedData sharedData ) {
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
