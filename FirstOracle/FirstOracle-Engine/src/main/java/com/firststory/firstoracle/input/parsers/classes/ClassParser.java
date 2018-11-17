/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SceneParser;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.exceptions.ParsedClassNotFoundException;
import com.firststory.firstoracle.input.parsers.NodeParser;
import com.firststory.firstoracle.input.structure.Leaf;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;

import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public abstract class ClassParser< Type > implements NodeParser< Class< ? extends Type >, Leaf > {
    
    private static final Logger logger = FirstOracleConstants.getLogger( ClassParser.class );
    
    abstract Class< Type > getBaseClass();
    
    abstract String getDefaultPackage();
    
    @Override
    public Class< ? extends Type > parse( Leaf node ) {
        var className = node.getValue();
        return classForName( className );
    }
    
    public Class< ? extends Type > classForName( String className ) {
        try {
            return SceneParser.class.getClassLoader()
                .loadClass( className )
                .asSubclass( getBaseClass() )
            ;
        } catch ( Exception e1 ) {
            logger.log( Level.WARNING, "Exception while extracting object class " + className, e1 );
            try {
                return ClassParser.class.getClassLoader()
                    .loadClass( getDefaultPackage() + "." + className )
                    .asSubclass( getBaseClass() )
                    ;
            } catch ( Exception e2 ) {
                e2.addSuppressed( e1 );
                throw new ParsedClassNotFoundException( className, getBaseClass(), e2 );
            }
        }
    }
    
    @SuppressWarnings( "unchecked" )
    public static Class< ? extends Terrain2D< ? > > getNewTerrain2DClass( String className ) {
        return getClass(
            className,
            Terrain2D.class,
            FirstOracleConstants.OBJECT_2D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    public static Class< ? extends Terrain3D< ? > > getNewTerrain3DClass( String className ) {
        return getClass(
            className,
            Terrain3D.class,
            FirstOracleConstants.OBJECT_3D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    public static Class< ? extends PositionableObject2D< ?, ? > > getNewObject2DClass( String className ) {
        return getClass(
            className,
            PositionableObject2D.class,
            FirstOracleConstants.OBJECT_2D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    public static Class< ? extends PositionableObject3D< ?, ? > > getNewObject3DClass( String className ) {
        return getClass(
            className,
            PositionableObject3D.class,
            FirstOracleConstants.OBJECT_3D_PACKAGE_NAME
        );
    }
    
    @SuppressWarnings( "unchecked" )
    public static Class< ? extends Terrain2D< ? > > getTerrain2DClass(
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
    public static Class< ? extends Terrain3D< ? > > getTerrain3DClass(
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
    public static Class< ? extends PositionableObject2D< ?, ? > > getObject2DClass(
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
    public static Class< ? extends PositionableObject3D< ?, ? > > getObject3DClass(
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
    public static <T> Class getClass(
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
    public static Class getClass(
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
