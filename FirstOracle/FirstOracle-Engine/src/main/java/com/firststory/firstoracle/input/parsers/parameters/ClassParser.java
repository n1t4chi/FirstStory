/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SceneParser;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.exceptions.ParsedClassNotFoundException;
import com.firststory.firstoracle.object2D.Position2DCalculator;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object3D.Position3DCalculator;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;

import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public interface ClassParser {
    
    Logger logger = FirstOracleConstants.getLogger( ClassParser.class );
    
    @SuppressWarnings( "unchecked" )
    static Class< Position2DCalculator > getNewPositionCalculatorClass2D( String className ) {
        return getClass(
            className,
            Position2DCalculator.class,
            FirstOracleConstants.POSITION_CALCULATOR_2D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static Class< Position3DCalculator > getNewPositionCalculatorClass3D( String className ) {
        return getClass(
            className,
            Position2DCalculator.class,
            FirstOracleConstants.POSITION_CALCULATOR_3D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static Class< ? extends Terrain2D< ? > > getNewTerrainClass2D( String className ) {
        return getClass(
            className,
            Terrain2D.class,
            FirstOracleConstants.OBJECT_2D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static Class< ? extends Terrain3D< ? > > getNewTerrainClass3D( String className ) {
        return getClass(
            className,
            Terrain3D.class,
            FirstOracleConstants.OBJECT_3D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static Class< ? extends PositionableObject2D< ?, ? > > getNewObjectClass2D( String className ) {
        return getClass(
            className,
            PositionableObject2D.class,
            FirstOracleConstants.OBJECT_2D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static Class< ? extends PositionableObject3D< ?, ? > > getNewObjectClass3D( String className ) {
        return getClass(
            className,
            PositionableObject3D.class,
            FirstOracleConstants.OBJECT_3D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static Class< ? extends Terrain2D< ? > > getTerrainClass2D(
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends Terrain2D< ? > > > sharedDataExtractor,
        String className
    ) {
        return getClass(
            className,
            sharedData,
            sharedDataExtractor,
            Terrain2D.class,
            FirstOracleConstants.OBJECT_2D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static Class< ? extends Terrain3D< ? > > getTerrainClass3D(
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends Terrain3D< ? > > > sharedDataExtractor,
        String className
    ) {
        return getClass(
            className,
            sharedData,
            sharedDataExtractor,
            Terrain3D.class,
            FirstOracleConstants.OBJECT_3D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static Class< ? extends PositionableObject2D< ?, ? > > getObjectClass2D(
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends PositionableObject2D< ?, ? > > > sharedDataExtractor,
        String className
    ) {
        return getClass(
            className,
            sharedData,
            sharedDataExtractor,
            PositionableObject2D.class,
            FirstOracleConstants.OBJECT_2D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static Class< ? extends PositionableObject3D< ?, ? > > getObjectClass3D(
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends PositionableObject3D< ?, ? > > > sharedDataExtractor,
        String className
    ) {
        return getClass(
            className,
            sharedData,
            sharedDataExtractor,
            PositionableObject3D.class,
            FirstOracleConstants.OBJECT_3D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    static <T> Class getClass(
        String className,
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends T > > sharedDataExtractor,
        Class aClass,
        String defaultPackage
    ) {
        return ParseUtils.getNewOrShared(
            className,
            sharedData,
            sharedDataExtractor,
            () -> getClass( className, aClass, defaultPackage )
        );
    }
    
    @SuppressWarnings( "rawtypes" )
    static Class getClass(
        String className,
        Class aClass,
        String defaultPackage
    ) {
        try {
            return SceneParser.class.getClassLoader().loadClass( className );
        } catch ( Exception e1 ) {
            logger.log( Level.WARNING, "Exception while extracting object class " + className, e1 );
            try {
                return ClassParser.class.getClassLoader()
                    .loadClass( defaultPackage + "." + className );
            } catch ( Exception e2 ) {
                e2.addSuppressed( e1 );
                throw new ParsedClassNotFoundException( className, aClass, e2 );
            }
        }
    }
}
