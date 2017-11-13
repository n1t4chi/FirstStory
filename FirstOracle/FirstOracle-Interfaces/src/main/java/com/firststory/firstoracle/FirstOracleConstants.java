/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle;

/**
 * @author n1t4chi
 */
public class FirstOracleConstants {

    public static final double SQRT3_DIV2_D = 0.8660254037844386467637231707529361834714026269051903140279034897259665084544000185405730933786242878378130707077;
    public static final float SQRT3_DIV2 = 0.8660254037844386467637231707529361834714026269051903140279034897259665084544000185405730933786242878378130707077f;

    public static float transCubeDiscreteToSpace( float coordinate, float terrainShift ) {
        return 2 * (coordinate+terrainShift);
    }

    public static float transPlaneDiscreteToSpace( float coordinate, float terrainShift ) {
        return 2 * (coordinate+terrainShift);
    }

    public static float transAbsolutePlaneDiscreteToSpace( float coordinate, float terrainShift ) {
        return (coordinate+terrainShift);
    }

    public static float transHexXDiscreteToSpace( float x, float terrainShift ) {
        return (x+terrainShift) * 1.5f;
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
        return (x+terrainShift) * 1.5f;
    }

    public static float transHexPrismYDiscreteToSpace( float y, float terrainShift ) {
        return (y+terrainShift) * 2;
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
        return  coordinate / 2 -terrainShift;
    }

    public static float transPlaneSpaceToDiscrete( float coordinate, float terrainShift ) {
        return  coordinate / 2 -terrainShift;
    }

    public static float transAbsolutePlaneSpaceToDiscrete( 
        float coordinate, float terrainShift 
    ) {
        return  coordinate -terrainShift;
    }

    public static float transHexXSpaceToDiscrete( float x, float terrainShift ) {
        return  x / 1.5f -terrainShift;
    }

    public static float transHexYSpaceToDiscrete( 
        float x, float y, float terrainShiftX, float terrainShiftY 
    ) {
        float x_sub = transHexXSpaceToDiscrete( x , terrainShiftX );
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

    public static float transHexPrismXSpaceToDiscrete( float x, float terrainShift ) {
        return  x / 1.5f -terrainShift;
    }

    public static float transHexPrismYSpaceToDiscrete( float y, float terrainShift ) {
        return  y / 2 -terrainShift;
    }

    public static float transHexPrismZSpaceToDiscrete( 
        float x, float z, float terrainShiftX, float terrainShiftZ 
    ) {
        float x_sub = transHexXSpaceToDiscrete( x , terrainShiftX );
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

}
