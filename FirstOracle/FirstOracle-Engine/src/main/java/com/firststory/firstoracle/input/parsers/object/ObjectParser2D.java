/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.parsers.classes.Object2DClassParser;
import com.firststory.firstoracle.input.parsers.classes.Terrain2DClassParser;
import com.firststory.firstoracle.input.parsers.parameters.TransformationParser;
import com.firststory.firstoracle.input.parsers.parameters.VerticesParser;
import com.firststory.firstoracle.input.structure.Leaf;
import com.firststory.firstoracle.object2D.*;

import java.util.List;

/**
 * @author n1t4chi
 */
public class ObjectParser2D extends ObjectParser<
    PositionableObject2D< ?, ? >,
    Terrain2D< ? >,
    Vertices2D,
    Position2D,
    Index2D,
    Object2DClassParser,
    Terrain2DClassParser
> {
    
    @Override
    void setPositionCalculator(
        Terrain2D< ? > terrain,
        SharedData sharedData,
        Leaf leaf
    ) {
        sharedData.getPosition2DCalculatorParser().apply(
            terrain,
            leaf
        );
    }
    
    @Override
    void setTransformation(
        PositionableObject2D< ?, ? > object,
        SharedData sharedData,
        Leaf position,
        Leaf rotation,
        Leaf scale
    ) {
        TransformationParser.setTransformations2D(
            object,
            sharedData,
            position,
            rotation,
            scale
        );
    }
    
    @Override
    VerticesParser< Vertices2D, Position2D > getVerticesParser( SharedData sharedData ) {
        return sharedData.getVertices2DParser();
    }
    
    public Terrain2DClassParser getTerrainClassParser(
        SharedData sharedData
    ) {
        return sharedData.getTerrain2DClassParser();
    }
    
    public Object2DClassParser getObjectClassParser(
        SharedData sharedData
    ) {
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
}
