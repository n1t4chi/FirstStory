/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author n1t4chi
 */
public class PropertiesUtil {
    public static final String DEBUG_PROPERTY = "debugMode";
    public static final String VULKAN_VALIDATION_LAYERS_ENABLED_PROPERTY = "VulkanValidationLayersEnabled";
    public static final String VULKAN_VALIDATION_LAYERS_LIST_PROPERTY = "VulkanValidationLayersList";
    public static final String VULKAN_USE_SRGB_PROPERTY = "VulkanUseSRGBIfPossible";
    public static final String APPLICATION_CLASS_NAME_PROPERTY = "ApplicationClassName";
    public static final String RENDERING_FRAMEWORK_CLASS_NAME_PROPERTY = "RenderingFrameworkClassName";
    public static final String DISABLE_TEXTURES_PROPERTY = "DisableTextures";
    public static final String FORCE_ONE_LOOP_CYCLE_PROPERTY = "ForceOneLoopCycle";
    public static final String RENDER_LOOP_PERFORMANCE_LOG_PROPERTY = "EnableRenderLoopPerformanceLog";
    private static final Logger logger = FirstOracleConstants.getLogger( PropertiesUtil.class );
    
    public static boolean isPropertyTrue( String propertyName ) {
        String value = System.getProperty( propertyName );
        if( value != null ){
            return Boolean.parseBoolean( value );
        }
        logger.warning( propertyNotSetMessage( propertyName, "boolean" ) );
        return false;
    }
    
    public static int getIntegerProperty( String propertyName, int defaultValue ) {
        return getGenericProperty( propertyName, defaultValue, "integer", Integer::parseInt );
    }
    
    public static float getFloatProperty( String propertyName, float defaultValue ) {
        return getGenericProperty( propertyName, defaultValue, "float", Float::parseFloat );
    }
    
    public static String getProperty( String propertyName, String defaultValue ) {
        return getGenericProperty( propertyName, defaultValue, "string", Function.identity() );
    }
    
    public static String getMatchingStringProperty( String propertyName, Pattern pattern, String defaultValue ) {
        return getGenericProperty( propertyName, defaultValue, "pattern: "+pattern,
            value -> {
                if(pattern.matcher( value ).matches())
                    return value;
                throw new RuntimeException( "Value does not match pattern." );
            }
        );
    }
    
    public static <T> T getGenericProperty(
        String propertyName, T defaultValue, String expectedValueInfo, Function< String, T > parser
    ){
        String value = System.getProperty( propertyName );
        
        if( value != null ) {
            try {
                return parser.apply( value );
            } catch ( Exception ex ) {
                logger.log( Level.WARNING, wrongPropertyMessage( propertyName, value, expectedValueInfo ), ex );
                return defaultValue;
            }
        }
        logger.warning( propertyNotSetMessage( propertyName, expectedValueInfo ) );
        return defaultValue;
    }
    
    public static List<String> getListFromProperty( String propertyName ){
        String property = System.getProperty( propertyName, null );
        if(property == null){
            throw new PropertyNotFoundException( propertyName );
        }
        property = property.trim();
        if( !property.matches( "\\[.*]" ) ){
            throw new InvalidPropertyFormatException(propertyName, property);
        }
        property = property.substring( 1, property.length()-1 );
        String[] properties = property.split( "," );
        return new ArrayList<>( Arrays.asList( properties ) );
    }
    
    public static boolean isDebugMode(){
        return Boolean.parseBoolean( System.getProperty( DEBUG_PROPERTY ) );
    }
    
    public static List< String > getListFromPropertySafe( String propertyName ) {
        try {
            return getListFromProperty( propertyName );
        } catch ( Exception ex ) {
            logger.log(
                Level.WARNING,"Cannot retrieve list for property: "+propertyName+". Returning empty list.", ex );
            return Collections.emptyList();
        }
    }
    
    private static String propertyNotSetMessage( String propertyName, String expectedValueInfo ) {
        return "Property "+propertyName+" is not set to expected "+ expectedValueInfo+" value. Using default one.";
    }
    
    private static String wrongPropertyMessage( String propertyName, String value, String expectedValueInfo ) {
        return "Property "+propertyName+" is set to wrong value: "+value+". Expected "+ expectedValueInfo+" value.";
    }
    
    private static class PropertyNotFoundException extends RuntimeException {
        
        PropertyNotFoundException( String propertyName ) {
            super( "Property "+propertyName +"" );
        }
    }
    
    private static class InvalidPropertyFormatException extends RuntimeException {
        
        InvalidPropertyFormatException( String propertyName, String property ) {
            super( "Property "+propertyName +"" );
        }
    }
}
