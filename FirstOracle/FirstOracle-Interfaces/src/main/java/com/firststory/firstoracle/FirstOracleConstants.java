/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.Colouring;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.text.TextData;
import com.firststory.firstoracle.text.TextImageFactory;
import org.joml.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static com.firststory.firstoracle.data.Colour.col;
import static com.firststory.firstoracle.data.Index2D.id2;
import static com.firststory.firstoracle.data.Index3D.id3;
import static com.firststory.firstoracle.data.Position2D.pos2;
import static com.firststory.firstoracle.data.Position3D.pos3;
import static com.firststory.firstoracle.data.Rotation2D.rot2;
import static com.firststory.firstoracle.data.Rotation3D.rot3;
import static com.firststory.firstoracle.data.Scale2D.scale2;
import static com.firststory.firstoracle.data.Scale3D.scale3;
import static com.firststory.firstoracle.data.UV.uv;

/**
 * @author n1t4chi
 */
public interface FirstOracleConstants {
    
    Logger logger = getLogger( FirstOracleConstants.class );
    
    String RESOURCES_FOLDER = "resources/";
    String RESOURCES_ORACLE_FOLDER = RESOURCES_FOLDER + "First Oracle/";
    String SHADER_FILES_LOCATION = RESOURCES_ORACLE_FOLDER + "shader/";
    String GET_FRAMEWORK_PROVIDER_METHOD_NAME = "getProvider";
    
    String REFLECT_PROVIDE_METHOD_NAME = "provide";
    
    int NO_FLAGS = 0;
    String FIRST_ORACLE = "FirstOracle";
    int FIRST_ORACLE_VERSION_MAJOR = 0;
    int FIRST_ORACLE_VERSION_MINOR = 5;
    int FIRST_ORACLE_VERSION_PATCH = 0;
    
    float UV_DELTA = 0.00001f;
    
    int BYTE_SIZE_BYTE = 1;
    int BYTE_SIZE_FLOAT = 4;
    int BYTE_SIZE_INT = 4;
    int FLOAT_SIZE_MATRIX_4F = 4*4;
    int FLOAT_SIZE_VEC_4F = 4;
    
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
    
    Position2D POSITION_ZERO_2F = pos2( 0, 0 );
    Position3D POSITION_ZERO_3F = pos3( 0, 0, 0 );
    
    Rotation2D ROTATION_ZERO_2F = rot2( 0 );
    Rotation3D ROTATION_ZERO_3F = rot3( 0, 0, 0 );
    
    Scale2D SCALE_ONE_2F = scale2( 1, 1 );
    Scale3D SCALE_ONE_3F = scale3( 1, 1, 1 );
    
    Index2D INDEX_ZERO_2I = id2( 0, 0 );
    Index3D INDEX_ZERO_3I = id3( 0, 0, 0 );
    
    Colour TRANSPARENT = col( 0, 0, 0, 0 );
    Colour RED = col( 1, 0, 0, 1 );
    Colour GREEN = col( 0, 1, 0, 1 );
    Colour BLUE = col( 0, 0, 1, 1 );
    Colour WHITE = col( 1, 1, 1, 1 );
    Colour BLACK = col( 0, 0, 0, 1 );
    Colour GRAY = col( 0.5f, 0.5f, 0.5f, 1 );
    Colour YELLOW = col( 1, 1, 0, 1 );
    
    LineData YELLOW_LINE_LOOP = LineData.lineLoop( 1, FirstOracleConstants.YELLOW );
    LineData RED_LINE_LOOP = LineData.lineLoop( 1, FirstOracleConstants.RED );
    LineData NONE = null;
    
    Colouring EMPTY_COLOURING = new Colouring( Collections.singletonList( col( 0, 0, 0, 0 ) ) );
    UvMap EMPTY_UV_MAP = new UvMap( singletonArray2D( Arrays.asList( uv( 0,0 ), uv( 1,1 ), uv( 1,0 ) ) ) );
    Texture EMPTY_TEXTURE = createEmptyTexture();
    
    double SQRT3_DIV2_D = 0.8660254037844386467637231707529361834714026269051903140279034897259665084544000185405730933786242878378130707077;
    float SQRT3_DIV2 = 0.8660254037844386467637231707529361834714026269051903140279034897259665084544000185405730933786242878378130707077f;
    float[] EMPTY_FLOAT_ARRAY = new float[ 0 ];
    
    TextData EMPTY_TEXT = TextImageFactory.provide().createText3D( "" );
    
    static <T> List<T>[][][] singletonArray3D( List<T> value ){
        List< T >[][][] array = array( 1, 1, 1 );
        array[0][0][0] = value;
        return array;
    }
    static <T> List<T>[][] singletonArray2D( List<T> value ){
        List< T >[][] array = array( 1, 1 );
        array[0][0] = value;
        return array;
    }
    static <T> List<T>[] singletonArray( List<T> value ){
        List< T >[] array = array( 1 );
        array[0] = value;
        return array;
    }
    
    @SuppressWarnings( "unchecked" )
    static <T> List<T>[][][] array( int x,int y, int z ){
        return new java.util.List[x][y][z];
    }
    
    @SuppressWarnings( "unchecked" )
    static <T> List<T>[][] array( int x,int y ){
        return new java.util.List[x][y];
    }
    
    @SuppressWarnings( "unchecked" )
    static <T> List<T>[] array( int x ){
        return new List[x];
    }
    
    static Texture createEmptyTexture() {
        var SIZE = 100;
        var image = new BufferedImage( SIZE, SIZE, BufferedImage.TYPE_INT_ARGB );
        var graphics = ( Graphics2D ) image.getGraphics();
        graphics.setColor( new Color(1,1,1,1) );
        graphics.fillRect( 0, 0, SIZE, SIZE );
        graphics.dispose();
        Texture texture;
        try {
            texture = Texture.create( image );
        } catch ( Exception e ) {
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
    
    static float transHexYDiscreteToSpace( float x, float y, float terrainShiftX, float terrainShiftY ) {
        var x_sum = x + terrainShiftX;
        double modulo = x_sum % 2;
    
        var translated = y + terrainShiftY;
        var product = 2 * translated;
        var sum = product + modulo;
        var finalProduct = sum * SQRT3_DIV2_D;
        
        return ( float ) finalProduct;
    }
    
    static float transHexPrismXDiscreteToSpace( float x, float terrainShift ) {
        return ( x + terrainShift ) * 1.5f;
    }
    
    static float transHexPrismYDiscreteToSpace( float y, float terrainShift ) {
        return ( y + terrainShift ) * 2;
    }
    
    static float transHexPrismZDiscreteToSpace( float x, float z, float terrainShiftX, float terrainShiftZ ) {
        var x_sum = x + terrainShiftX;
        double modulo = x_sum % 2;
    
        var translated = z + terrainShiftZ;
        var product = 2 * translated;
        var sum = product + modulo;
        var finalProduct = sum * SQRT3_DIV2_D;
        
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
    
    static float transHexYSpaceToDiscrete( float x, float y, float terrainShiftX, float terrainShiftY ) {
        var x_sub = transHexXSpaceToDiscrete( x, terrainShiftX );
        double modulo = x_sub % 2;
    
        var initialDivision = y / SQRT3_DIV2_D;
        var subtraction = initialDivision - modulo;
        var division = subtraction / 2;
        var finalTranslation = division - terrainShiftY;
        
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
    
    static float transHexPrismZSpaceToDiscrete( float x, float z, float terrainShiftX, float terrainShiftZ ) {
        var x_sub = transHexXSpaceToDiscrete( x, terrainShiftX );
        double modulo = x_sub % 2;
    
        var initialDivision = z / SQRT3_DIV2_D;
        var subtraction = initialDivision - modulo;
        var division = subtraction / 2;
        var finalTranslation = division - terrainShiftZ;
        
        return ( float ) finalTranslation;
    }
    
    static Logger getLogger( Class< ? > classObject ) {
        return Logger.getLogger( classObject.getName() );
    }
    
}
