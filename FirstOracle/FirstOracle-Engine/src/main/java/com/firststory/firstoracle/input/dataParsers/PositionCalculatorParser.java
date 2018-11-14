/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.dataParsers;

import com.firststory.firstoracle.input.Exceptions.ParseFailedException;
import com.firststory.firstoracle.input.Exceptions.ParsedClassNotFoundException;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.object.Terrain;
import com.firststory.firstoracle.object2D.Position2DCalculator;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object3D.Position3DCalculator;
import com.firststory.firstoracle.object3D.Terrain3D;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.firststory.firstoracle.input.ParseUtils.METHOD_SET_POSITION_CALCULATOR;

/**
 * @author n1t4chi
 */
public class PositionCalculatorParser {
    
    
    public static Position2DCalculator newCalculator2D( String calculatorName ) {
        return newCalculator(
            calculatorName,
            Position2DCalculator.class,
            ClassParser::getNewPositionCalculatorClass2D
        );
    }
    
    public static Position3DCalculator newCalculator3D( String calculatorName ) {
        return newCalculator(
            calculatorName,
            Position3DCalculator.class,
            ClassParser::getNewPositionCalculatorClass3D
        );
    }
    
    public static void setCalculator2D(
        Terrain2D< ? > terrain,
        SharedData sharedData,
        String calculatorName
    ) {
        setCalculator(
            terrain,
            sharedData,
            calculatorName,
            SharedData::getPosition2DCalculator,
            Position2DCalculator.class,
            ClassParser::getNewPositionCalculatorClass2D
        );
    }
    
    public static void setCalculator3D(
        Terrain3D< ? > terrain,
        SharedData sharedData,
        String calculatorName
    ) {
        setCalculator(
            terrain,
            sharedData,
            calculatorName,
            SharedData::getPosition3DCalculator,
            Position3DCalculator.class,
            ClassParser::getNewPositionCalculatorClass3D
        );
    }
    
    private static < TerrainType extends Terrain< ?, ?, ?, ?, ? >, Calculator > void setCalculator(
        TerrainType terrain,
        SharedData sharedData,
        String calculatorName,
        BiFunction< SharedData, String, Calculator > getter,
        Class< Calculator > aClass,
        Function< String, Class< Calculator > > provider
    ) {
        if( calculatorName == null ) {
            return;
        }
        try {
            terrain.getClass()
                .getMethod( METHOD_SET_POSITION_CALCULATOR, aClass )
                .invoke( terrain, newCalculator(
                    sharedData,
                    calculatorName,
                    aClass,
                    getter,
                    provider
                ) );
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
    }
    
    private static < Calculator > Calculator newCalculator(
        SharedData sharedData,
        String calculatorName,
        Class< Calculator > aClass,
        BiFunction< SharedData, String, Calculator > getter,
        Function< String, Class< Calculator > > provider
    ) {
        return ParseUtils.getNewOrShared(
            calculatorName,
            sharedData,
            getter,
            () -> newCalculator( calculatorName, aClass, provider )
        );
    }
    
    @SuppressWarnings( "unchecked" )
    private static < Calculator > Calculator newCalculator(
        String calculatorName,
        Class< Calculator > aClass,
        Function< String, Class< Calculator > > provider
    ) {
        try {
            return provider.apply( calculatorName )
                .getDeclaredConstructor()
                .newInstance()
            ;
        } catch ( Exception ex ) {
            throw new ParsedClassNotFoundException( calculatorName, aClass, ex );
        }
    }
}
