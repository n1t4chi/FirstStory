/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.parsers.parameters.ClassParser;
import com.firststory.firstoracle.input.parsers.parameters.PositionCalculatorParser;
import com.firststory.firstoracle.input.parsers.parameters.TransformationParser;
import com.firststory.firstoracle.input.parsers.parameters.VerticesParser;
import com.firststory.firstoracle.object2D.*;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author n1t4chi
 */
public class ObjectParser2D extends ObjectParser<
    PositionableObject2D< ?, ? >,
    Terrain2D< ? >,
    Vertices2D,
    Position2D,
    Index2D
> {
    
    @Override
    void setPositionCalculator(
        Terrain2D< ? > terrain,
        SharedData sharedData2,
        String calculatorName
    ) {
        PositionCalculatorParser.setCalculator2D( terrain,
            sharedData2,
            calculatorName
        );
    }
    
    @Override
    void setTransformation(
        PositionableObject2D< ?, ? > object1,
        SharedData sharedData2,
        String position,
        String rotation,
        String scale
    ) {
        TransformationParser.setTransformations2D( object1,
            sharedData2,
            position,
            rotation,
            scale
        );
    }
    
    @Override
    VerticesParser< Vertices2D, Position2D > getVerticesParser( SharedData sharedData ) {
        return sharedData.getVertices2DParser();
    }
    
    public Class< ? extends Terrain2D< ? > > getTerrainClass(
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends Terrain2D< ? > > > sharedDataExtractor,
        String className
    ) {
        return ClassParser.getTerrainClass2D( sharedData, sharedDataExtractor, className );
    }
    
    public Class< ? extends Terrain2D< ? > > extractTerrainClass(
        SharedData sharedData2,
        String key
    ) {
        return sharedData2.getTerrainClass2D( key );
    }
    
    @Override
    Class< ? extends PositionableObject2D< ?, ? > > extractObjectClass(
        SharedData sharedData,
        String className
    ) {
        return sharedData.getObjectClass2D( className );
    }
    
    @Override
    Class< ? extends PositionableObject2D< ?, ? > > getObjectClass(
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends PositionableObject2D< ?, ? > > > sharedDataExtractor,
        String className
    ) {
        return ClassParser
            .getObjectClass2D( sharedData,
                sharedDataExtractor,
                className
            );
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
