/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.objectParsers;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.parameters.ClassParser;
import com.firststory.firstoracle.input.parameters.PositionCalculatorParser;
import com.firststory.firstoracle.input.parameters.TransformationParser;
import com.firststory.firstoracle.input.parameters.VerticesParser;
import com.firststory.firstoracle.object3D.*;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author n1t4chi
 */
public class ObjectParser3D extends ObjectParser<
    Object3D< ?, ? >,
    PositionableObject3D< ?, ? >,
    Terrain3D< ? >,
    Index3D
> {
    @Override
    void setVertices(
        Object3D< ?, ? > object,
        SharedData sharedData1,
        String verticesText
    ) {
        VerticesParser.setVertices3D( object,
            sharedData1,
            verticesText
        );
    }
    
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
