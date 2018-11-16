/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.parsers.parameters.ClassParser;
import com.firststory.firstoracle.input.parsers.parameters.PositionCalculatorParser;
import com.firststory.firstoracle.input.parsers.parameters.TransformationParser;
import com.firststory.firstoracle.input.parsers.parameters.VerticesParser;
import com.firststory.firstoracle.object3D.*;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author n1t4chi
 */
public class ObjectParser3D extends ObjectParser<
    PositionableObject3D< ?, ? >,
    Terrain3D< ? >,
    Vertices3D,
    Position3D,
    Index3D
> {
    
    @Override
    void setTransformation(
        PositionableObject3D< ?, ? > object1,
        SharedData sharedData2,
        String position,
        String rotation,
        String scale
    ) {
        TransformationParser.setTransformations3D( object1,
            sharedData2,
            position,
            rotation,
            scale
        );
    }
    
    @Override
    VerticesParser< Vertices3D, Position3D > getVerticesParser( SharedData sharedData ) {
        return sharedData.getVertices3DParser();
    }
    
    @Override
    void setPositionCalculator(
        Terrain3D< ? > terrain,
        SharedData sharedData1,
        String calculatorName
    ) {
        PositionCalculatorParser.setCalculator3D( terrain,
            sharedData1,
            calculatorName
        );
    }
    
    public Class< ? extends Terrain3D< ? > > getTerrainClass(
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends Terrain3D< ? > > > sharedDataExtractor,
        String className
    ) {
        return ClassParser.getTerrainClass3D( sharedData,
                sharedDataExtractor,
                className
            );
    }
    
    public Class< ? extends Terrain3D< ? > > extractTerrainClass(
        SharedData sharedData,
        String key
    ) {
        return sharedData.getTerrainClass3D( key );
    }
    
    public Class< ? extends PositionableObject3D< ?, ? > > extractObjectClass(
        SharedData sharedData,
        String key
    ) {
        return sharedData.getObjectClass3D( key );
    }
    
    public Class< ? extends PositionableObject3D< ?, ? > > getObjectClass(
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends PositionableObject3D< ?, ? > > > sharedDataExtractor,
        String className
    ) {
        return ClassParser
            .getObjectClass3D(
                sharedData,
                sharedDataExtractor,
                className
            );
    }
    
    @Override
    List< Index3D > parseIndex( String text ) {
        return ParseUtils.toIndices3D( text );
    }
    
    @Override
    Class< PositionableObject3DImpl > getDefaultObjectClass() {
        return PositionableObject3DImpl.class;
    }
    
    @Override
    Class< Terrain3DImpl > getDefaultTerrainClass() {
        return Terrain3DImpl.class;
    }
}
