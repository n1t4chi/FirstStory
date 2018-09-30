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
public interface PropertiesUtil {
    
    String DEBUG_PROPERTY = "debugMode";
    
    String VULKAN_VALIDATION_LAYERS_ENABLED_PROPERTY = "VulkanValidationLayersEnabled";
    String VULKAN_VALIDATION_LAYERS_LIST_PROPERTY = "VulkanValidationLayersList";
    String VULKAN_USE_SRGB_PROPERTY = "VulkanUseSRGBIfPossible";
    
    String APPLICATION_CLASS_NAME_PROPERTY = "ApplicationClassName";
    String RENDERING_FRAMEWORK_CLASS_NAME_PROPERTY = "RenderingFrameworkClassName";
    String WINDOW_FRAMEWORK_CLASS_NAME_PROPERTY = "WindowFrameworkClassName";
    String GUI_FRAMEWORK_CLASS_NAME_PROPERTY = "GuiFrameworkClassName";
    
    String DISABLE_TEXTURES_PROPERTY = "DisableTextures";
    String DRAW_BORDER_PROPERTY = "DrawBorder";
    
    String FORCE_ONE_LOOP_CYCLE_PROPERTY = "ForceOneLoopCycle";
    String RENDER_LOOP_PERFORMANCE_LOG_PROPERTY = "EnableRenderLoopPerformanceLog";
    
    String WINDOW_RENDERER_GRID_2D_RENDERER_CLASS_NAME_PROPERTY = "grid2DRendererClassName";
    String WINDOW_RENDERER_GRID_3D_RENDERER_CLASS_NAME_PROPERTY = "grid3DRendererClassName";
    
    Logger logger = FirstOracleConstants.getLogger( PropertiesUtil.class );
    
    static boolean isPropertyTrue( String propertyName ) {
        return getGenericProperty( propertyName, false, "boolean", Boolean::parseBoolean );
    }
    
    static int getIntegerProperty( String propertyName, int defaultValue ) {
        return getGenericProperty( propertyName, defaultValue, "integer", Integer::parseInt );
    }
    
    static float getFloatProperty( String propertyName, float defaultValue ) {
        return getGenericProperty( propertyName, defaultValue, "float", Float::parseFloat );
    }
    
    static String getProperty( String propertyName, String defaultValue ) {
        return getGenericProperty( propertyName, defaultValue, "string", Function.identity() );
    }
    
    static String getProperty( String propertyName ) {
        return getGenericProperty( propertyName, null, "string", Function.identity() );
    }
    
    static String getMatchingStringProperty( String propertyName, Pattern pattern, String defaultValue ) {
        return getGenericProperty( propertyName, defaultValue, "pattern: " + pattern, value -> {
            if ( !pattern.matcher( value ).matches() ) {
                throw new RuntimeException( "Value does not match pattern." );
            }
            return value;
        } );
    }
    
    static < T > T getGenericProperty(
        String propertyName, T defaultValue, String expectedValueInfo, Function< String, T > parser
    ) {
        var value = System.getProperty( propertyName );
        
        if ( value == null ) {
            logger.warning( propertyNotSetMessage( propertyName, expectedValueInfo ) );
            return defaultValue;
        }
        
        try {
            return parser.apply( value );
        } catch ( Exception ex ) {
            logger.log( Level.WARNING, wrongPropertyMessage( propertyName, value, expectedValueInfo ), ex );
            return defaultValue;
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
    
    static boolean isDebugMode() {
        return Boolean.parseBoolean( System.getProperty( DEBUG_PROPERTY ) );
    }
    
    static List< String > getListFromProperty( String propertyName ) {
        try {
            return getListFromPropertyUnsafe( propertyName );
        } catch ( Exception ex ) {
            logger.log( Level.WARNING, "Cannot retrieve list for property: " + propertyName + ". Returning empty list.", ex );
            return Collections.emptyList();
        }
    }
    
    private static String propertyNotSetMessage( String propertyName, String expectedValueInfo ) {
        return "Property " + propertyName + " is not set to expected " + expectedValueInfo + " value. Using default value.";
    }
    
    private static String wrongPropertyMessage( String propertyName, String value, String expectedValueInfo ) {
        return "Property " + propertyName + " is set to wrong value: " + value + ". Expected " + expectedValueInfo + " value.";
    }
    
    class PropertyNotFoundException extends RuntimeException {
        
        PropertyNotFoundException( String propertyName ) {
            super( "Property " + propertyName + "" );
        }
    }
    
    class InvalidPropertyFormatException extends RuntimeException {
        
        InvalidPropertyFormatException( String propertyName, String property ) {
            super( "Property " + propertyName + "" );
        }
    }
}
