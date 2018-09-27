/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle;/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */

import org.junit.Assert;
import org.junit.Test;

/**
 * @author n1t4chi
 */
public class FirstOracleConstantsTest {
    
    final int[] testCasesInt = {
        -666,
        -10, -2, -1, 0, 1, 2, 10
        , 666
    };
    final float[] testCasesFloat = {
        -666.666f,
        -3, -1.5f, -1, -0.001f, -0.1f, -0, 0, +0, 0.1f, 0.001f, 1, 1.5f, 3
        , 666.666f
    };
    
    @Test
    public void transCube() {
        for ( var startValue : testCasesInt ) {
            assertTransformCube( startValue );
        }
        for ( var startValue : testCasesFloat ) {
            assertTransformCube( startValue );
        }
    }
    
    @Test
    public void transPlane() {
        for ( var startValue : testCasesInt ) {
            assertTransformPlane( startValue );
        }
        for ( var startValue : testCasesFloat ) {
            assertTransformPlane( startValue );
        }
    }
    
    @Test
    public void transAbsolutePlane() {
        for ( var startValue : testCasesInt ) {
            assertAbsoluteTransformPlane( startValue );
        }
        for ( var startValue : testCasesFloat ) {
            assertAbsoluteTransformPlane( startValue );
        }
    }
    
    @Test
    public void transHexX() {
        for ( var startValue : testCasesInt ) {
            assertHexX( startValue );
        }
        for ( var startValue : testCasesFloat ) {
            assertHexX( startValue );
        }
    }
    
    @Test
    public void transHexY() {
        for ( var startValue : testCasesInt ) {
            assertHexY( startValue );
        }
        for ( var startValue : testCasesFloat ) {
            assertHexY( startValue );
        }
    }
    
    @Test
    public void transHexPrismX() {
        for ( var startValue : testCasesInt ) {
            assertHexPrismX( startValue );
        }
        for ( var startValue : testCasesFloat ) {
            assertHexPrismX( startValue );
        }
    }
    
    @Test
    public void transHexPrismY() {
        for ( var startValue : testCasesInt ) {
            assertHexPrismY( startValue );
        }
        for ( var startValue : testCasesFloat ) {
            assertHexPrismY( startValue );
        }
    }
    
    @Test
    public void transHexPrismZ() {
        for ( var startValue : testCasesInt ) {
            assertHexPrismZ( startValue );
        }
        for ( var startValue : testCasesFloat ) {
            assertHexPrismZ( startValue );
        }
    }
    
    private void assertTransformCube( int startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transCubeSpaceToDiscrete( FirstOracleConstants.transCubeDiscreteToSpace(
                startValue,
                0
            ), 0 ),
            0.001f
        );
    }
    
    private void assertTransformCube( float startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transCubeDiscreteToSpace( FirstOracleConstants.transCubeSpaceToDiscrete(
                startValue,
                0
            ), 0 ),
            0.001f
        );
    }
    
    private void assertTransformPlane( int startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transPlaneSpaceToDiscrete( FirstOracleConstants.transPlaneDiscreteToSpace(
                startValue,
                0
            ), 0 ),
            0.001f
        );
    }
    
    private void assertTransformPlane( float startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transPlaneDiscreteToSpace( FirstOracleConstants.transPlaneSpaceToDiscrete(
                startValue,
                0
            ), 0 ),
            0.001f
        );
    }
    
    private void assertAbsoluteTransformPlane( int startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transAbsolutePlaneSpaceToDiscrete( FirstOracleConstants.transAbsolutePlaneDiscreteToSpace(
                startValue,
                0
            ), 0 ),
            0.001f
        );
    }
    
    private void assertAbsoluteTransformPlane( float startValue ) {
        
        Assert.assertEquals( startValue,
            FirstOracleConstants.transAbsolutePlaneDiscreteToSpace( FirstOracleConstants.transAbsolutePlaneSpaceToDiscrete(
                startValue,
                0
            ), 0 ),
            0.001f
        );
    }
    
    private void assertHexX( int startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexXSpaceToDiscrete( FirstOracleConstants.transHexXDiscreteToSpace(
                startValue,
                0
            ), 0 ),
            0.001f
        );
    }
    
    private void assertHexX( float startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexXDiscreteToSpace( FirstOracleConstants.transHexXSpaceToDiscrete(
                startValue,
                0
            ), 0 ),
            0.001f
        );
    }
    
    private void assertHexY( int startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexYSpaceToDiscrete( 0,
                FirstOracleConstants.transHexYDiscreteToSpace( 0, startValue, 0, 0 ),
                0,
                0
            ),
            0.001f
        );
        
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexYSpaceToDiscrete( 1.5f,
                FirstOracleConstants.transHexYDiscreteToSpace( 1, startValue, 0, 0 ),
                0,
                0
            ),
            0.001f
        );
    }
    
    private void assertHexY( float startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexYDiscreteToSpace( 0,
                FirstOracleConstants.transHexYSpaceToDiscrete( 0, startValue, 0, 0 ),
                0,
                0
            ),
            0.001f
        );
        
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexYDiscreteToSpace( 1,
                FirstOracleConstants.transHexYSpaceToDiscrete( 1.5f, startValue, 0, 0 ),
                0,
                0
            ),
            0.001f
        );
    }
    
    private void assertHexPrismX( int startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexPrismXSpaceToDiscrete( FirstOracleConstants.transHexPrismXDiscreteToSpace(
                startValue,
                0
            ), 0 ),
            0.001f
        );
    }
    
    private void assertHexPrismX( float startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexPrismXDiscreteToSpace( FirstOracleConstants.transHexPrismXSpaceToDiscrete(
                startValue,
                0
            ), 0 ),
            0.001f
        );
    }
    
    private void assertHexPrismY( int startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexPrismYSpaceToDiscrete( FirstOracleConstants.transHexPrismYDiscreteToSpace(
                startValue,
                0
            ), 0 ),
            0.001f
        );
    }
    
    private void assertHexPrismY( float startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexPrismYDiscreteToSpace( FirstOracleConstants.transHexPrismYSpaceToDiscrete(
                startValue,
                0
            ), 0 ),
            0.0001f
        );
    }
    
    private void assertHexPrismZ( int startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexPrismZSpaceToDiscrete( 0,
                FirstOracleConstants.transHexPrismZDiscreteToSpace( 0, startValue, 0, 0 )
                , 0,
                0
            ), 0.001f
        );
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexPrismZSpaceToDiscrete( 1.5f,
                FirstOracleConstants.transHexPrismZDiscreteToSpace( 1, startValue, 0, 0 )
                , 0,
                0
            ), 0.001f
        );
    }
    
    private void assertHexPrismZ( float startValue ) {
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexPrismZDiscreteToSpace( 0,
                FirstOracleConstants.transHexPrismZSpaceToDiscrete( 0, startValue, 0, 0 )
                , 0,
                0
            ),
            0.001f
        );
        Assert.assertEquals( startValue,
            FirstOracleConstants.transHexPrismZDiscreteToSpace( 1,
                FirstOracleConstants.transHexPrismZSpaceToDiscrete( 1.5f, startValue, 0, 0 )
                , 0,
                0
            ),
            0.001f
        );
    }
}