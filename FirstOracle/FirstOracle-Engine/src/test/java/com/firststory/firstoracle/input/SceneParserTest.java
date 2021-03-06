/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.object3D.*;
import com.firststory.firstoracle.rendering.RenderType;
import com.firststory.firstoracle.scene.*;
import org.junit.jupiter.api.*;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.FirstOracleConstants.*;
import static com.firststory.firstoracle.data.Position2D.pos2;
import static com.firststory.firstoracle.data.Position3D.pos3;
import static com.firststory.firstoracle.data.Rotation2D.rot2;
import static com.firststory.firstoracle.data.Rotation3D.rot3;
import static com.firststory.firstoracle.data.Scale2D.scale2;
import static com.firststory.firstoracle.data.Scale3D.scale3;

class SceneParserTest {
    
    private MockAttributeLoader mockLoader = new MockAttributeLoader();
    
    @Test
    void parseEmptyScene() {
        var scene2D = SceneParser.parseToNonOptimised2D( "" );
        var scene3D = SceneParser.parseToNonOptimised3D( "" );
        assertSize2D( scene2D,  0, INDEX_ZERO_2I );
        assertSize3D( scene3D, 0, INDEX_ZERO_3I );
    }
    
    @Test
    void parseOneObject2d() {
        var scene2D = SceneParser.parseToNonOptimised2D( `{
            "scene2D": {
                "objects": {
                    "object1": {
                        "class": "PositionableObject2DImpl",
                        "transformations": "MutablePositionable2DTransformations",
                        "position": "4, 3",
                        "rotation": "45",
                        "scale": "2, 6",
                        "texture": "resources/First Oracle/textures/grass2D.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1}, {2,2}, {3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]"
                    }
                }
            }
        }` );
        assertSize2D( scene2D,  1, INDEX_ZERO_2I );
        assertObject(
            scene2D.getObjects2D().iterator().next(),
            pos2( 4, 3 ),
            rot2( 45 ),
            scale2( 2, 6 ),
            PositionableObject2DImpl.class,
            "resources/First Oracle/textures/grass2D.png",
            new float[]{ 1,1,0, 2,2,0, 3,3,0 },
            new float[]{ 1,1, 2,2, 3,3 },
            new float[]{ 1,1,1,1, 2,2,2,2, 3,3,3,3 }
        );
    }
    
    @Test
    void parseOneObject3d() {
        var scene3D = SceneParser.parseToNonOptimised3D( `{
            "scene3D": {
                "objects": {
                    "object1": {
                        "class": "PositionableObject3DImpl",
                        "transformations": "MutablePositionable3DTransformations",
                        "position": "4, 3, 2",
                        "rotation": "10, 20, 30",
                        "scale": "2, 6, 12",
                        "texture": "resources/First Oracle/textures/grass2D.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]"
                    }
                }
            }
        }` );
        assertSize3D( scene3D,  1, INDEX_ZERO_3I );
        
        assertObject(
            scene3D.getObjects3D().iterator().next(),
            pos3( 4, 3, 2 ),
            rot3( 10, 20, 30 ),
            scale3( 2, 6, 12 ),
            PositionableObject3DImpl.class,
            "resources/First Oracle/textures/grass2D.png",
            new float[]{ 1,1,1, 2,2,2, 3,3,3 },
            new float[]{ 1,1, 2,2, 3,3 },
            new float[]{ 1,1,1,1, 2,2,2,2, 3,3,3,3 }
        );
    }
    
//    @Test
    void parseOneTerrain2d() {
        var scene2D = SceneParser.parseToNonOptimised2D( `{
            "configuration": {
                "terrain2DShift": "{3,3}",
                "terrain2DSize": "{1,3}"
            },
            "scene2D": {
                "terrains": {
                    "object1": {
                        "class": "Terrain2DImpl",
                        "texture": "resources/First Oracle/textures/grass2D.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1}, {2,2}, {3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]",
                        "indices": "[ {0,2} ]",
                        "positionCalculator": "RectanglePositionCalculator"
                    }
                }
            }
        }` );
        Assertions.assertEquals( Index2D.id2( 3, 3 ), scene2D.getTerrain2DShift() );
        assertSize2D( scene2D, 0, Index2D.id2( 1, 3 ) );
        Assertions.assertNull( scene2D.getTerrains2D()[0][0] );
        Assertions.assertNull( scene2D.getTerrains2D()[0][1] );
        assertTerrain(
            scene2D.getTerrains2D()[0][2],
            Index2D.id2( 0,2 ),
            scene2D.getTerrain2DShift(),
            RectanglePositionCalculator.instance.indexToPosition( 3, 5, FirstOracleConstants.INDEX_ZERO_2I ),
            Terrain2DImpl.class,
            "resources/First Oracle/textures/grass2D.png",
            new float[]{ 1,1,0, 2,2,0, 3,3,0 },
            new float[]{ 1,1, 2,2, 3,3 },
            new float[]{ 1,1,1,1, 2,2,2,2, 3,3,3,3 }
        );
    }
    
//    @Test
    void parseOneTerrain3d() {
        var scene3D = SceneParser.parseToNonOptimised3D( `{
            "configuration": {
                "terrain3DShift": "{3,3,3}",
                "terrain3DSize": "{1,1,3}"
            },
            "scene3D": {
                "terrains": {
                    "object1": {
                        "class": "Terrain3DImpl",
                        "texture": "resources/First Oracle/textures/grass2D.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]",
                        "indices": "[ {0,0,2} ]",
                        "positionCalculator": "CubePositionCalculator"
                    }
                }
            }
        }` );
//        Assertions.assertEquals( Index3D.id3( 3, 3, 3 ), scene3D.getTerrain3DShift() );
        assertSize3D( scene3D, 0, Index3D.id3( 1, 1, 3 ) );
        Assertions.assertNull( scene3D.getTerrains3D()[0][0][0] );
        Assertions.assertNull( scene3D.getTerrains3D()[0][0][1] );
        assertTerrain(
            scene3D.getTerrains3D()[0][0][2],
            Index3D.id3( 0,0, 2 ),
            scene3D.getTerrain3DShift(),
            CubePositionCalculator.instance.indexToPosition( 3, 3, 5, FirstOracleConstants.INDEX_ZERO_3I ),
            Terrain3DImpl.class,
            "resources/First Oracle/textures/grass2D.png",
            new float[]{ 1,1,1, 2,2,2, 3,3,3 },
            new float[]{ 1,1, 2,2, 3,3 },
            new float[]{ 1,1,1,1, 2,2,2,2, 3,3,3,3 }
        );
    }
    
    @Test
    void parseSharedData() {
        var text = `{
            "sharedData": {
                "objectClasses2D": {
                    "obj2d": "PositionableObject2DImpl"
                },
                "positionableTransformations2D": {
                    "trans2d": "MutablePositionable2DTransformations"
                },
                "positionableTransformations3D": {
                    "trans3d": "MutablePositionable3DTransformations"
                },
                "terrainClasses2D": {
                    "terr2d": "Terrain2DImpl"
                },
                "positions2D": {
                    "pos2d": "4, 3"
                },
                "rotations2D": {
                    "rot2d": "45"
                },
                "scales2D": {
                    "scl2d": "2, 6"
                },
                "vertices2D": {
                    "vert2d": "[ [ {1,1}, {2,2}, {3,3} ] ]"
                },
                "objectClasses3D": {
                    "obj3d": "PositionableObject3DImpl"
                },
                "terrainClasses3D": {
                    "terr3d": "Terrain3DImpl"
                },
                "positions3D": {
                    "pos3d": "4, 3, 2"
                },
                "rotations3D": {
                    "rot3d": "10, 20, 30"
                },
                "scales3D": {
                    "scl3d": "2, 6, 12"
                },
                "vertices3D": {
                    "vert3d": "[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]"
                },
                "textures": {
                    "tex": "resources/First Oracle/textures/grass2D.png"
                },
                "uvMaps": {
                    "uv": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]"
                },
                "colourings": {
                    "col": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]"
                }
            },
            "scene2D": {
                "objects": {
                    "object1": {
                        "class": "$obj2d",
                        "transformations": "$trans2d",
                        "position": "$pos2d",
                        "rotation": "$rot2d",
                        "scale": "$scl2d",
                        "texture": "$tex",
                        "uvMap": "$uv",
                        "vertices": "$vert2d",
                        "colouring": "$col"
                    },
                    "object2": {
                        "class": "$obj2d",
                        "transformations": "$trans2d",
                        "position": "$pos2d",
                        "rotation": "$rot2d",
                        "scale": "$scl2d",
                        "texture": "$tex",
                        "uvMap": "$uv",
                        "vertices": "$vert2d",
                        "colouring": "$col"
                    }
                },
                "terrains": {
                    "terrain1": {
                        "class": "$terr2d",
                        "texture": "$tex",
                        "uvMap": "$uv",
                        "vertices": "$vert2d",
                        "colouring": "$col",
                        "indices": "[ {0,0}x{0,2}, {1,2} ]",
                        "positionCalculator": "RectanglePositionCalculator"
                    }
                }
            },
            "scene3D": {
                "objects": {
                    "object3": {
                        "class": "$obj3d",
                        "transformations": "$trans3d",
                        "position": "$pos3d",
                        "rotation": "$rot3d",
                        "scale": "$scl3d",
                        "texture": "$tex",
                        "uvMap": "$uv",
                        "vertices": "$vert3d",
                        "colouring": "$col"
                    },
                    "object4": {
                        "class": "$obj3d",
                        "transformations": "$trans3d",
                        "position": "$pos3d",
                        "rotation": "$rot3d",
                        "scale": "$scl3d",
                        "texture": "$tex",
                        "uvMap": "$uv",
                        "vertices": "$vert3d",
                        "colouring": "$col"
                    }
                },
                "terrains": {
                    "terrain2": {
                        "class": "$terr3d",
                        "texture": "$tex",
                        "uvMap": "$uv",
                        "vertices": "$vert3d",
                        "colouring": "$col",
                        "indices": "[ {0,0,0}x{0,0,2} ]",
                        "positionCalculator": "CubePositionCalculator"
                    }
                }
            }
        }`;
        var scene2D = SceneParser.parseToNonOptimised2D( text );
        var scene3D = SceneParser.parseToNonOptimised3D( text );
        Assertions.assertEquals( Index3D.id3( 0, 0, 0 ), scene3D.getTerrain3DShift() );
        assertSize2D( scene2D, 2, Index2D.id2( 2,3 ) );
        assertSize3D( scene3D, 2, Index3D.id3( 1,1,3 ) );
        
        float[] expectedUvMap = { 1, 1, 2, 2, 3, 3 };
        float[] expectedVertices2D = { 1, 1, 0, 2, 2, 0, 3, 3, 0 };
        float[] expectedVertices3D = { 1, 1, 1, 2, 2, 2, 3, 3, 3 };
        float[] expectedColouring = { 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3 };
        var expectedTexturePath = "resources/First Oracle/textures/grass2D.png";
        
        
        scene2D.getObjects2D().forEach( object -> assertObject(
            object,
            pos2( 4, 3 ),
            rot2( 45 ),
            scale2( 2, 6 ),
            PositionableObject2DImpl.class,
            expectedTexturePath,
            expectedVertices2D,
            expectedUvMap,
            expectedColouring
        ) );
        
        scene3D.getObjects3D().forEach( object ->
            assertObject(
                object,
                pos3( 4, 3, 2 ),
                rot3( 10, 20, 30 ),
                scale3( 2, 6, 12 ),
                PositionableObject3DImpl.class,
                expectedTexturePath,
                expectedVertices3D,
                expectedUvMap,
                expectedColouring
            )
        );
        
        Assertions.assertNull( scene2D.getTerrains2D()[1][0] );
        Assertions.assertNull( scene2D.getTerrains2D()[1][1] );
        
        assertTerrain(
            scene2D.getTerrains2D()[1][2],
            Index2D.id2( 1,2 ),
            scene2D.getTerrain2DShift(),
            RectanglePositionCalculator.instance.indexToPosition( 1, 2, FirstOracleConstants.INDEX_ZERO_2I ),
            Terrain2DImpl.class,
            expectedTexturePath,
            expectedVertices2D,
            expectedUvMap,
            expectedColouring
        );
        for( var i=0; i< 3; i++ ) {
            assertTerrain(
                scene2D.getTerrains2D()[0][i],
                Index2D.id2( 0,i ),
                scene2D.getTerrain2DShift(),
                RectanglePositionCalculator.instance.indexToPosition( 0, i, FirstOracleConstants.INDEX_ZERO_2I ),
                Terrain2DImpl.class,
                expectedTexturePath,
                expectedVertices2D,
                expectedUvMap,
                expectedColouring
            );
            assertTerrain(
                scene3D.getTerrains3D()[0][0][i],
                Index3D.id3( 0, 0, i ),
                scene3D.getTerrain3DShift(),
                CubePositionCalculator.instance.indexToPosition( 0, 0, i, FirstOracleConstants.INDEX_ZERO_3I ),
                Terrain3DImpl.class,
                expectedTexturePath,
                expectedVertices3D,
                expectedUvMap,
                expectedColouring
            );
        }
    }
    
    
    
    
    @Test
    void parseSharedObjects() {
        var text = `{
            "sharedObjects": {
                "objects2D": {
                    "baseObj2d": {
                        "class": "PositionableObject2DImpl",
                        "transformations": "MutablePositionable2DTransformations",
                        "rotation": "45",
                        "scale": "2, 6",
                        "texture": "resources/First Oracle/textures/grass2D.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1}, {2,2}, {3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]"
                    }
                },
                "terrains2D": {
                    "baseTer2d": {
                        "class": "Terrain2DImpl",
                        "texture": "resources/First Oracle/textures/grass2D.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1}, {2,2}, {3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]",
                        "positionCalculator": "RectanglePositionCalculator"
                    }
                },
                "objects3D": {
                    "baseObj3d": {
                        "class": "PositionableObject3DImpl",
                        "transformations": "MutablePositionable3DTransformations",
                        "rotation": "10, 20, 30",
                        "scale": "2, 6, 12",
                        "texture": "resources/First Oracle/textures/grass2D.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]"
                    }
                },
                "terrains3D": {
                    "baseTer3d": {
                        "class": "Terrain3DImpl",
                        "texture": "resources/First Oracle/textures/grass2D.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]",
                        "positionCalculator": "CubePositionCalculator"
                    }
                }
            },
            "scene2D": {
                "objects": {
                    "object1": {
                        "base": "$baseObj2d",
                        "positions": "[{1,1},{2,2}]"
                    }
                },
                "terrains": {
                    "terrain1": {
                        "base": "$baseTer2d",
                        "indices": "[ {0,0}x{2,2} ]"
                    }
                }
            },
            "scene3D": {
                "objects": {
                    "object3": {
                        "base": "$baseObj3d",
                        "positions": "[{1,1,1},{2,2,2}]"
                    }
                },
                "terrains": {
                    "terrain2": {
                        "base": "$baseTer3d",
                        "indices": "[ {0,0,0}x{2,2,2} ]"
                    }
                }
            }
        }`;
        var scene2D = SceneParser.parseToNonOptimised2D( text );
        var scene3D = SceneParser.parseToNonOptimised3D( text );
        Assertions.assertEquals( Index2D.id2( 0, 0 ), scene2D.getTerrain2DShift() );
        Assertions.assertEquals( Index3D.id3( 0, 0, 0 ), scene3D.getTerrain3DShift() );
        assertSize2D( scene2D, 2, Index2D.id2( 3,3 ) );
        assertSize3D( scene3D, 2, Index3D.id3( 3,3,3 ) );
        
        float[] expectedUvMap = { 1, 1, 2, 2, 3, 3 };
        float[] expectedVertices2D = { 1, 1, 0, 2, 2, 0, 3, 3, 0 };
        float[] expectedVertices3D = { 1, 1, 1, 2, 2, 2, 3, 3, 3 };
        float[] expectedColouring = { 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3 };
        var expectedTexturePath = "resources/First Oracle/textures/grass2D.png";
        var i = 1;
        var sortedObjects2d = scene2D.getObjects2D().stream()
            .sorted( ( o1, o2 ) -> Float.compare( o1.getPosition().x(), o2.getPosition().x() ) )
            .collect( Collectors.toList() )
        ;
        for ( var positionableObject2D : sortedObjects2d ) {
            assertObject(
                positionableObject2D,
                pos2( i, i ),
                rot2( 45 ),
                scale2( 2, 6 ),
                PositionableObject2DImpl.class,
                expectedTexturePath,
                expectedVertices2D,
                expectedUvMap,
                expectedColouring
            );
            i++;
        }
        i = 1;
        var sortedObjects3d = scene3D.getObjects3D().stream()
            .sorted( ( o1, o2 ) -> Float.compare( o1.getPosition().x(), o2.getPosition().x() ) )
            .collect( Collectors.toList() )
            ;
        for ( var object : sortedObjects3d ) {
            assertObject(
                object,
                pos3( i, i, i ),
                rot3( 10, 20, 30 ),
                scale3( 2, 6, 12 ),
                PositionableObject3DImpl.class,
                expectedTexturePath,
                expectedVertices3D,
                expectedUvMap,
                expectedColouring
            );
            i++;
        }
        for( var x=0; x< 2; x++ ) {
            for ( var y = 0; y < 2; y++ ) {
                assertTerrain(
                    scene2D.getTerrains2D()[x][y],
                    Index2D.id2( x,y ),
                    scene2D.getTerrain2DShift(),
                    RectanglePositionCalculator.instance.indexToPosition( x, y, FirstOracleConstants.INDEX_ZERO_2I ),
                    Terrain2DImpl.class,
                    expectedTexturePath,
                    expectedVertices2D,
                    expectedUvMap,
                    expectedColouring
                );
            }
        }
        for( var x=0; x< 2; x++ ) {
            for ( var y = 0; y < 2; y++ ) {
                for ( var z = 0; z < 2; z++ ) {
                    assertTerrain(
                        scene3D.getTerrains3D()[x][y][z],
                        Index3D.id3( x, y, z ),
                        scene3D.getTerrain3DShift(),
                        CubePositionCalculator.instance.indexToPosition( x, y, z, FirstOracleConstants.INDEX_ZERO_3I ),
                        Terrain3DImpl.class,
                        expectedTexturePath,
                        expectedVertices3D,
                        expectedUvMap,
                        expectedColouring
                    );
                }
            }
        }
    }
    
    private < IndexType extends Index, PositionType extends Position > void assertTerrain(
        Terrain< PositionType, ?, ?, ?, ?, ?, IndexType, ? > terrain,
        IndexType index,
        IndexType shift,
        PositionType expectedCalculatedPosition,
        Class< ? extends Terrain< ?, ?, ?, ?, ?, ?, ?, ? > > expectedClass,
        String expectedTexturePath,
        float[] expectedVertices,
        float[] expectedUvMap,
        float[] expectedColouring
    ) {
        Assertions.assertNotNull( terrain );
        var data = terrain.getRenderData( terrain.getPositionCalculator().indexToPosition( index, shift ), 0, 0 ).stream()
            .filter( renderData -> renderData.getType() == RenderType.TRIANGLES )
            .findFirst()
            .orElse( null );
        Assertions.assertNotNull( data );
        Assertions.assertEquals( expectedClass, terrain.getClass() );
        Assertions.assertEquals( expectedCalculatedPosition, data.getPosition() );
        Assertions.assertEquals( expectedTexturePath, data.getTexture().getData().getName() );
        Assertions.assertArrayEquals(
            expectedColouring,
            data.getColouring().getBuffer( mockLoader ).getBufferData()
        );
        Assertions.assertArrayEquals(
            expectedVertices,
            data.getVertices().getBuffer( mockLoader, 0 ).getBufferData()
        );
        Assertions.assertArrayEquals(
            expectedUvMap,
            data.getUvMap().getBuffer( mockLoader, 0, 0 ).getBufferData()
        );
    }
    
    private void assertObject(
        PositionableObject< ?, ?, ?, ?, ?, ? > object,
        Position expectedPosition,
        Rotation expectedRotation,
        Scale expectedScale,
        Class< ? extends PositionableObject< ?, ?, ?, ?, ?, ? > > expectedClass,
        String expectedTexturePath,
        float[] expectedVertices,
        float[] expectedUvMap,
        float[] expectedColouring
    ) {
        var data = object.getRenderData( 0, 0 ).stream()
            .filter( renderData -> renderData.getType() == RenderType.TRIANGLES )
            .findFirst()
            .orElse( null );
        Assertions.assertNotNull( data );
        Assertions.assertEquals( expectedClass, object.getClass() );
        Assertions.assertEquals( expectedPosition, data.getPosition() );
        Assertions.assertEquals( expectedRotation, data.getRotation() );
        Assertions.assertEquals( expectedScale, data.getScale() );
        Assertions.assertEquals( expectedTexturePath, data.getTexture().getData().getName() );
        Assertions.assertArrayEquals(
            expectedColouring,
            data.getColouring().getBuffer( mockLoader ).getBufferData()
        );
        Assertions.assertArrayEquals(
            expectedVertices,
            data.getVertices().getBuffer( mockLoader, 0 ).getBufferData()
        );
        Assertions.assertArrayEquals(
            expectedUvMap,
            data.getUvMap().getBuffer( mockLoader, 0, 0 ).getBufferData()
        );
    }
    
    private void assertSize2D(
        RegistrableScene2DImpl scene2D,
        int expectedObjects2dSize,
        Index2D expectedTerrains2dSize
    ) {
        var objects2dSize = scene2D.getObjects2D().size();
        var terrains2dSize = arraySize( scene2D.getTerrains2D() );
        
        Assertions.assertTrue(
            Objects.equals( expectedObjects2dSize, objects2dSize ) &&
            Objects.equals( expectedTerrains2dSize, terrains2dSize ),
            () ->
                "Expected: " +
                    "2d" + toStr( expectedObjects2dSize, expectedTerrains2dSize ) + "\n" +
                    "Actual: " +
                    "2d" + toStr( objects2dSize, terrains2dSize )
        );
    }
    
    private void assertSize3D(
        RegistrableScene3DImpl scene3D,
        int expectedObjects3dSize,
        Index3D expectedTerrains3dSize
    ) {
        var objects3dSize = scene3D.getObjects3D().size();
        var terrains3dSize = arraySize( scene3D.getTerrains3D() );
        
        Assertions.assertTrue(
            Objects.equals( expectedObjects3dSize, objects3dSize ) &&
            Objects.equals( expectedTerrains3dSize, terrains3dSize ),
            () ->
                "Expected: " +
                    "3d" + toStr( expectedObjects3dSize, expectedTerrains3dSize ) + "\n" +
                "Actual: " +
                    "3d" + toStr( objects3dSize, terrains3dSize )
        );
    }
    
    private String toStr( int objSize, Index2D terSize ){
        return toStr(  objSize, terSize.x() + "," + terSize.y() );
    }
    
    private String toStr( int objSize, Index3D terSize ){
        return toStr(  objSize, terSize.x() + "," + terSize.y() + "," + terSize.z() );
    }
    
    private String toStr( int objSize, String ter ){
        return "( obj: " + objSize + ", terr:[" + ter + "] )";
    }
}