/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle;

import com.firststory.firstoracle.object.Colour;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.rendering.LineData;
import org.joml.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public interface FirstOracleConstants {
    
    Logger logger = getLogger( FirstOracleConstants.class );
    
    String RESOURCES_FOLDER = "resources/";
    String RESOURCES_ORACLE_FOLDER = RESOURCES_FOLDER + "First Oracle/";
    String SHADER_FILES_LOCATION = RESOURCES_ORACLE_FOLDER + "shader/";
    String GET_FRAMEWORK_METHOD_NAME = "getFramework";
    
    int NO_FLAGS = 0;
    String FIRST_ORACLE = "FirstOracle";
    int FIRST_ORACLE_VERSION_MAJOR = 0;
    int FIRST_ORACLE_VERSION_MINOR = 4;
    int FIRST_ORACLE_VERSION_PATCH = 0;
    
    float UV_DELTA = 0.00001f;
    
    int SIZE_FLOAT = 4;
    int SIZE_INT = 4;
    int SIZE_MATRIX_4F = 4 * 4;
    int SIZE_VEC_4F = 4;
    
    Matrix4fc MATRIX_4F_IDENTIFY = new Matrix4f();
    
    Vector2fc VECTOR_ZERO_2F = new Vector2f( 0, 0 );
    Vector2ic VECTOR_ZERO_2I = new Vector2i( 0, 0 );
    Vector3fc VECTOR_ZERO_3F = new Vector3f( 0, 0, 0 );
    Vector3ic VECTOR_ZERO_3I = new Vector3i( 0, 0, 0 );
    Vector4fc VECTOR_ZERO_4F = new Vector4f( 0, 0, 0, 0 );
    
    Vector2fc VECTOR_ONES_2F = new Vector2f( 1, 1 );
    Vector2ic VECTOR_ONES_2I = new Vector2i( 1, 1 );
    Vector3fc VECTOR_ONES_3F = new Vector3f( 1, 1, 1 );
    Vector3ic VECTOR_ONES_3I = new Vector3i( 1, 1, 1 );
    Vector4fc VECTOR_ONES_4F = new Vector4f( 1, 1, 1, 1 );
    
    Vector4fc TRANSPARENT = new Vector4f( 0, 0, 0, 0 );
    Vector4fc RED = new Vector4f( 1, 0, 0, 1 );
    Vector4fc GREEN = new Vector4f( 0, 1, 0, 1 );
    Vector4fc BLUE = new Vector4f( 0, 0, 1, 1 );
    Vector4fc WHITE = new Vector4f( 1, 1, 1, 1 );
    Vector4fc BLACK = new Vector4f( 0, 0, 0, 1 );
    Vector4fc GRAY = new Vector4f( 0.5f, 0.5f, 0.5f, 1 );
    
    LineData RED_LINE = new LineData( 1, FirstOracleConstants.RED );
    LineData NONE = null;
    
    Colour EMPTY_COLOUR = new Colour( new float[]{ 0, 0, 0, 0 } );
    UvMap EMPTY_UV_MAP = new UvMap( new float[ 1 ][ 1 ][ 3 ] );
    Texture EMPTY_TEXTURE = createEmptyTexture();
    double SQRT3_DIV2_D = 0.8660254037844386467637231707529361834714026269051903140279034897259665084544000185405730933786242878378130707077;
    float SQRT3_DIV2 = 0.8660254037844386467637231707529361834714026269051903140279034897259665084544000185405730933786242878378130707077f;
    
    static Texture createEmptyTexture() {
        BufferedImage image = new BufferedImage( 50, 50, BufferedImage.TYPE_INT_ARGB );
        Graphics graphics = image.getGraphics();
        graphics.fillRect( 0, 0, 50, 50 );
        graphics.dispose();
        Texture texture;
        try {
            texture = new Texture( image );
        } catch ( IOException e ) {
            throw new RuntimeException( "Cannot create empty texture!", e );
        }
        return texture;
    }
    
    static float transCubeDiscreteToSpace( float coordinate, float terrainShift ) {
        return 2 * ( coordinate + terrainShift );
    }
    
    static float transPlaneDiscreteToSpace( float coordinate, float terrainShift ) {
        return 2 * ( coordinate + terrainShift );
    }
    
    static float transAbsolutePlaneDiscreteToSpace( float coordinate, float terrainShift ) {
        return ( coordinate + terrainShift );
    }
    
    static float transHexXDiscreteToSpace( float x, float terrainShift ) {
        return ( x + terrainShift ) * 1.5f;
    }
    
    static float transHexYDiscreteToSpace(
        float x, float y, float terrainShiftX, float terrainShiftY
    )
    {
        float x_sum = x + terrainShiftX;
        double modulo = x_sum % 2;
        
        float translated = y + terrainShiftY;
        float product = 2 * translated;
        double sum = product + modulo;
        double finalProduct = sum * SQRT3_DIV2_D;

//        System.err.println( "d2s:"+  modulo+", "+translated+", "+product+", "+sum+", "+finalProduct );
        
        return ( float ) finalProduct;
    }
    
    static float transHexPrismXDiscreteToSpace( float x, float terrainShift ) {
        return ( x + terrainShift ) * 1.5f;
    }
    
    static float transHexPrismYDiscreteToSpace( float y, float terrainShift ) {
        return ( y + terrainShift ) * 2;
    }
    
    static float transHexPrismZDiscreteToSpace(
        float x, float z, float terrainShiftX, float terrainShiftZ
    )
    {
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
    
    static float transCubeSpaceToDiscrete( float coordinate, float terrainShift ) {
        return coordinate / 2 - terrainShift;
    }
    
    static float transPlaneSpaceToDiscrete( float coordinate, float terrainShift ) {
        return coordinate / 2 - terrainShift;
    }
    
    static float transAbsolutePlaneSpaceToDiscrete(
        float coordinate, float terrainShift
    )
    {
        return coordinate - terrainShift;
    }
    
    static float transHexYSpaceToDiscrete(
        float x, float y, float terrainShiftX, float terrainShiftY
    )
    {
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
    
    static float transHexXSpaceToDiscrete( float x, float terrainShift ) {
        return x / 1.5f - terrainShift;
    }
    
    static float transHexPrismXSpaceToDiscrete( float x, float terrainShift ) {
        return x / 1.5f - terrainShift;
    }
    
    static float transHexPrismYSpaceToDiscrete( float y, float terrainShift ) {
        return y / 2 - terrainShift;
    }
    
    static float transHexPrismZSpaceToDiscrete(
        float x, float z, float terrainShiftX, float terrainShiftZ
    )
    {
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
    
    static Logger getLogger( Class classObject ) {
        return Logger.getLogger( classObject.getName() );
    }
    
}
