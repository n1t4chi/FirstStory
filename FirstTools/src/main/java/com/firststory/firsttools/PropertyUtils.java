package com.firststory.firsttools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public interface PropertyUtils {

    Logger logger = FirstToolsConstants.getLogger( PropertyUtils.class );
    String DEBUG_PROPERTY = "debugMode";

    static boolean isDebugMode() {
        return Boolean.parseBoolean( System.getProperty( DEBUG_PROPERTY ) );
    }

    static boolean isPropertyTrue( String propertyName ) {
        return getGenericPropertyOrDefault( propertyName, false, "boolean", Boolean::parseBoolean );
    }

    static int getIntegerProperty( String propertyName, int defaultValue ) {
        return getGenericPropertyOrDefault( propertyName, defaultValue, "integer", Integer::parseInt );
    }

    static float getFloatProperty( String propertyName, float defaultValue ) {
        return getGenericPropertyOrDefault( propertyName, defaultValue, "float", Float::parseFloat );
    }

    static String getProperty( String propertyName, String defaultValue ) {
        return getGenericPropertyOrDefault( propertyName, defaultValue, "string", Function.identity() );
    }

    static String getProperty( String propertyName ) {
        return getGenericPropertyOrDefault( propertyName, null, "string", Function.identity() );
    }

    static String getPropertyOrThrow( String propertyName ) {
        return getGenericPropertyOrSupplyDefault(
            propertyName,
            () -> { throw new PropertyNotFoundException( propertyName ); },
            "string",
            Function.identity()
        );
    }

    static String getMatchingStringProperty( String propertyName, Pattern pattern, String defaultValue ) {
        return getGenericPropertyOrDefault( propertyName, defaultValue, "pattern: " + pattern, value -> {
            if ( !pattern.matcher( value ).matches() ) {
                throw new RuntimeException( "Value does not match pattern." );
            }
            return value;
        } );
    }

    static < T > T getGenericPropertyOrDefault(
        String propertyName,
        T defaultValue,
        String expectedValueInfo,
        Function< String, T > parser
    ) {
        return getGenericPropertyOrSupplyDefault( propertyName, () -> defaultValue, expectedValueInfo, parser );
    }


    static < T > T getGenericPropertyOrSupplyDefault(
        String propertyName,
        Supplier< T > defaultValueSupplier,
        String expectedValueInfo,
        Function< String, T > parser
    ) {
        var value = System.getProperty( propertyName );

        if ( value == null ) {
            logger.warning( propertyNotSetMessage( propertyName, expectedValueInfo ) );
            return defaultValueSupplier.get();
        }

        try {
            return parser.apply( value );
        } catch ( Exception ex ) {
            logger.log( Level.WARNING, wrongPropertyMessage( propertyName, value, expectedValueInfo ), ex );
            return defaultValueSupplier.get();
        }
    }

    static List< String > getListFromPropertyUnsafe( String propertyName ) {
        var property = System.getProperty( propertyName, null );
        if ( property == null ) {
            throw new PropertyNotFoundException( propertyName );
        }
        property = property.trim();
        if ( !property.matches( "\\[.*]" ) ) {
            throw new InvalidPropertyFormatException( propertyName, property );
        }
        property = property.substring( 1, property.length() - 1 );
        var properties = property.split( "," );
        return new ArrayList<>( Arrays.asList( properties ) );
    }

    static List< String > getListFromProperty( String propertyName ) {
        try {
            return getListFromPropertyUnsafe( propertyName );
        } catch ( Exception ex ) {
            logger.log( Level.WARNING,
                "Cannot retrieve list for property: " + propertyName + ". Returning empty list.",
                ex
            );
            return Collections.emptyList();
        }
    }
    
    private static String propertyNotSetMessage( String propertyName, String expectedValueInfo ) {
        return "Property " + propertyName + " is not set to expected " + expectedValueInfo +
               " value type. Using default value.";
    }

    private static String wrongPropertyMessage( String propertyName, String value, String expectedValueInfo ) {
        return "Property " + propertyName + " is set to wrong value: " + value + ". Expected " + expectedValueInfo +
               " value type.";
    }

    class PropertyNotFoundException extends RuntimeException {

        PropertyNotFoundException( String propertyName ) {
            super( "Property " + propertyName + "" );
        }
    }

    class InvalidPropertyFormatException extends RuntimeException {

        InvalidPropertyFormatException( String propertyName, String property ) {
            super( "Property " + propertyName + ", value: " + property );
        }
    }
}