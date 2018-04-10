/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle;

import org.joml.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public class FirstOracleConstants {
    public static final String DEBUG_PROPERTY = "debugMode";
    public static final String VULKAN_VALIDATION_LAYERS_ENABLED_PROPERTY = "VulkanValidationLayersEnabled";
    public static final String VULKAN_VALIDATION_LAYERS_LIST_PROPERTY = "VulkanValidationLayersList";
    public static final String APPLICATION_CLASS_NAME_PROPERTY = "ApplicationClassName";
    public static final String RENDERING_FRAMEWORK_CLASS_NAME_PROPERTY = "RenderingFrameworkClassName";
    public static final String DISABLE_TEXTURES_PROPERTY = "DisableTextures";
    
    public static final String GET_FRAMEWORK_METHOD_NAME = "getFramework";
    
    public static final int NO_FLAGS = 0;
    public static final int FIRST_STORY_VERSION_MAJOR = 0;
    public static final int FIRST_STORY_VERSION_MINOR = 1;
    public static final int FIRST_STORY_VERSION_PATCH = 0;
    public static final int FIRST_ORACLE_VERSION_MAJOR = 0;
    public static final int FIRST_ORACLE_VERSION_MINOR = 4;
    public static final int FIRST_ORACLE_VERSION_PATCH = 0;
    
    public static boolean isDebugMode(){
        return Boolean.getBoolean( System.getProperty( DEBUG_PROPERTY ) );
    }
    
    public static final float UV_DELTA = 0.00001f;
    public static final Vector2fc VECTOR_ZERO_2F = new Vector2f( 0, 0 );
    public static final Vector2ic VECTOR_ZERO_2I = new Vector2i( 0, 0 );
    public static final Vector3fc VECTOR_ZERO_3F = new Vector3f( 0, 0, 0 );
    public static final Vector3ic VECTOR_ZERO_3I = new Vector3i( 0, 0, 0 );
    public static final Vector4fc VECTOR_ZERO_4F = new Vector4f( 0, 0, 0, 0 );
    
    public static final double SQRT3_DIV2_D = 0.8660254037844386467637231707529361834714026269051903140279034897259665084544000185405730933786242878378130707077;
    public static final float SQRT3_DIV2 = 0.8660254037844386467637231707529361834714026269051903140279034897259665084544000185405730933786242878378130707077f;
    
    public static float transCubeDiscreteToSpace( float coordinate, float terrainShift ) {
        return 2 * ( coordinate + terrainShift );
    }
    
    public static float transPlaneDiscreteToSpace( float coordinate, float terrainShift ) {
        return 2 * ( coordinate + terrainShift );
    }
    
    public static float transAbsolutePlaneDiscreteToSpace( float coordinate, float terrainShift ) {
        return ( coordinate + terrainShift );
    }
    
    public static float transHexXDiscreteToSpace( float x, float terrainShift ) {
        return ( x + terrainShift ) * 1.5f;
    }
    
    public static float transHexYDiscreteToSpace(
        float x, float y, float terrainShiftX, float terrainShiftY
    ) {
        float x_sum = x + terrainShiftX;
        double modulo = x_sum % 2;
        
        float translated = y + terrainShiftY;
        float product = 2 * translated;
        double sum = product + modulo;
        double finalProduct = sum * SQRT3_DIV2_D;

//        System.err.println( "d2s:"+  modulo+", "+translated+", "+product+", "+sum+", "+finalProduct );
        
        return ( float ) finalProduct;
    }
    
    public static float transHexPrismXDiscreteToSpace( float x, float terrainShift ) {
        return ( x + terrainShift ) * 1.5f;
    }
    
    public static float transHexPrismYDiscreteToSpace( float y, float terrainShift ) {
        return ( y + terrainShift ) * 2;
    }
    
    public static float transHexPrismZDiscreteToSpace(
        float x, float z, float terrainShiftX, float terrainShiftZ
    ) {
        float x_sum = x + terrainShiftX;
        double modulo = x_sum % 2;
        
        float translated = z + terrainShiftZ;
        float product = 2 * translated;
        double sum = product + modulo;
        double finalProduct = sum * SQRT3_DIV2_D;

//        System.err.println( "d2s:" +
//                            "\nparams: " +x+", "+z+", "+terrainShiftX+", "+terrainShiftZ+
//                            "\nresults: "+ modulo+", "+translated+", "+product+", "+sum+", "+finalProduct );
        
        return ( float ) finalProduct;
    }
    
    public static float transCubeSpaceToDiscrete( float coordinate, float terrainShift ) {
        return coordinate / 2 - terrainShift;
    }
    
    public static float transPlaneSpaceToDiscrete( float coordinate, float terrainShift ) {
        return coordinate / 2 - terrainShift;
    }
    
    public static float transAbsolutePlaneSpaceToDiscrete(
        float coordinate, float terrainShift
    ) {
        return coordinate - terrainShift;
    }
    
    public static float transHexYSpaceToDiscrete(
        float x, float y, float terrainShiftX, float terrainShiftY
    ) {
        float x_sub = transHexXSpaceToDiscrete( x, terrainShiftX );
        double modulo = x_sub % 2;
        
        double initialDivision = y / SQRT3_DIV2_D;
        double subtraction = initialDivision - modulo;
        double division = subtraction / 2;
        double finalTranslation = division - terrainShiftY;

//        System.err.println("s2d:"+
//                           "\nparams: " +x+", "+y+", "+terrainShiftX+", "+terrainShiftY+
//            "\nresults: "+ modulo+", "+initialDivision+", "+subtraction+", "+division+", "+finalTranslation );
        
        return ( float ) finalTranslation;
    }
    
    public static float transHexXSpaceToDiscrete( float x, float terrainShift ) {
        return x / 1.5f - terrainShift;
    }
    
    public static float transHexPrismXSpaceToDiscrete( float x, float terrainShift ) {
        return x / 1.5f - terrainShift;
    }
    
    public static float transHexPrismYSpaceToDiscrete( float y, float terrainShift ) {
        return y / 2 - terrainShift;
    }
    
    public static float transHexPrismZSpaceToDiscrete(
        float x, float z, float terrainShiftX, float terrainShiftZ
    ) {
        float x_sub = transHexXSpaceToDiscrete( x, terrainShiftX );
        double modulo = x_sub % 2;
        
        double initialDivision = z / SQRT3_DIV2_D;
        double subtraction = initialDivision - modulo;
        double division = subtraction / 2;
        double finalTranslation = division - terrainShiftZ;

//        System.err.println( "s2d:"+
//                            "\nparams: " +x+", "+z+", "+terrainShiftX+", "+terrainShiftZ+
//                            "\nresults: "+ modulo+", "+initialDivision+", "+subtraction+", "+division+", "+finalTranslation );
        
        return ( float ) finalTranslation;
    }
    
    public static boolean isPropertyTrue( String propertyName ) {
        return Boolean.parseBoolean( System.getProperty( propertyName, "false" ) );
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
        return Arrays.asList( properties );
    }
    
    public static Logger getLogger( Class classObject ) {
        return Logger.getLogger( classObject.getName() );
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
