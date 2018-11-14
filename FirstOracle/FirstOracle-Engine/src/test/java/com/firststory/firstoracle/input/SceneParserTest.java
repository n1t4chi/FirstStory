/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.PositionableObject;
import com.firststory.firstoracle.object.Terrain;
import com.firststory.firstoracle.object2D.PlanePositionCalculator;
import com.firststory.firstoracle.object2D.PositionableObject2DImpl;
import com.firststory.firstoracle.object2D.Terrain2DImpl;
import com.firststory.firstoracle.object3D.CubePositionCalculator;
import com.firststory.firstoracle.object3D.PositionableObject3DImpl;
import com.firststory.firstoracle.object3D.Terrain3DImpl;
import com.firststory.firstoracle.rendering.RenderType;
import com.firststory.firstoracle.scene.RegistrableScene2DImpl;
import com.firststory.firstoracle.scene.RegistrableScene3DImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

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
        var scenePair = SceneParser.parseToNonOptimised( "" );
        var scene2D = scenePair.getScene2D();
        var scene3D = scenePair.getScene3D();
        assertSizes( scene2D, scene3D, 0, INDEX_ZERO_2I, 0, INDEX_ZERO_3I );
    }
    
    @Test
    void parseOneObject2d() {
        var scenePair = SceneParser.parseToNonOptimised( `{
            "scene2D": {
                "objects": {
                    "object1": {
                        "class": "PositionableObject2DImpl",
                        "position": "4, 3",
                        "rotation": "45",
                        "scale": "2, 6",
                        "texture": "resources/First Oracle/textures/grass.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1}, {2,2}, {3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]"
                    }
                }
            }
        }` );
        var scene2D = scenePair.getScene2D();
        var scene3D = scenePair.getScene3D();
        assertSizes( scene2D, scene3D, 1, INDEX_ZERO_2I, 0, INDEX_ZERO_3I );
        assertObject(
            scene2D.getObjects2D().iterator().next(),
            pos2( 4, 3 ),
            rot2( 45 ),
            scale2( 2, 6 ),
            PositionableObject2DImpl.class,
            "resources/First Oracle/textures/grass.png",
            new float[]{ 1,1,0, 2,2,0, 3,3,0 },
            new float[]{ 1,1, 2,2, 3,3 },
            new float[]{ 1,1,1,1, 2,2,2,2, 3,3,3,3 }
        );
    }
    
    @Test
    void parseOneObject3d() {
        var scenePair = SceneParser.parseToNonOptimised( `{
            "scene3D": {
                "objects": {
                    "object1": {
                        "class": "PositionableObject3DImpl",
                        "position": "4, 3, 2",
                        "rotation": "10, 20, 30",
                        "scale": "2, 6, 12",
                        "texture": "resources/First Oracle/textures/grass.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]"
                    }
                }
            }
        }` );
        var scene2D = scenePair.getScene2D();
        var scene3D = scenePair.getScene3D();
        assertSizes( scene2D, scene3D, 0, INDEX_ZERO_2I, 1, INDEX_ZERO_3I );
        
        assertObject(
            scene3D.getObjects3D().iterator().next(),
            pos3( 4, 3, 2 ),
            rot3( 10, 20, 30 ),
            scale3( 2, 6, 12 ),
            PositionableObject3DImpl.class,
            "resources/First Oracle/textures/grass.png",
            new float[]{ 1,1,1, 2,2,2, 3,3,3 },
            new float[]{ 1,1, 2,2, 3,3 },
            new float[]{ 1,1,1,1, 2,2,2,2, 3,3,3,3 }
        );
    }
    
    @Test
    void parseOneTerrain2d() {
        var scenePair = SceneParser.parseToNonOptimised( `{
            "configuration": {
                "terrain2DShift": "{3,3}",
                "terrain2DSize": "{1,3}"
            },
            "scene2D": {
                "terrains": {
                    "object1": {
                        "class": "Terrain2DImpl",
                        "texture": "resources/First Oracle/textures/grass.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1}, {2,2}, {3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]",
                        "indices": "[ {0,2} ]",
                        "positionCalculator": "PlanePositionCalculator"
                    }
                }
            }
        }` );
        var scene2D = scenePair.getScene2D();
        var scene3D = scenePair.getScene3D();
        Assertions.assertEquals( Index2D.id2( 3, 3 ), scene2D.getTerrain2DShift() );
        assertSizes( scene2D, scene3D, 0, Index2D.id2( 1, 3 ), 0, INDEX_ZERO_3I );
        Assertions.assertNull( scene2D.getTerrains2D()[0][0] );
        Assertions.assertNull( scene2D.getTerrains2D()[0][1] );
        assertTerrain(
            scene2D.getTerrains2D()[0][2],
            Index2D.id2( 0,2 ),
            scene2D.getTerrain2DShift(),
            PlanePositionCalculator.computePlanePosition( 3, 5, FirstOracleConstants.INDEX_ZERO_2I ),
            Terrain2DImpl.class,
            "resources/First Oracle/textures/grass.png",
            new float[]{ 1,1,0, 2,2,0, 3,3,0 },
            new float[]{ 1,1, 2,2, 3,3 },
            new float[]{ 1,1,1,1, 2,2,2,2, 3,3,3,3 }
        );
    }
    
    @Test
    void parseOneTerrain3d() {
        var scenePair = SceneParser.parseToNonOptimised( `{
            "configuration": {
                "terrain3DShift": "{3,3,3}",
                "terrain3DSize": "{1,1,3}"
            },
            "scene3D": {
                "terrains": {
                    "object1": {
                        "class": "Terrain3DImpl",
                        "texture": "resources/First Oracle/textures/grass.png",
                        "uvMap": "[ [ [ {1,1},  {2,2}, {3,3} ] ] ]",
                        "vertices": "[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]",
                        "colouring": "[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]",
                        "indices": "[ {0,0,2} ]",
                        "positionCalculator": "CubePositionCalculator"
                    }
                }
            }
        }` );
        var scene2D = scenePair.getScene2D();
        var scene3D = scenePair.getScene3D();
        Assertions.assertEquals( Index3D.id3( 3, 3, 3 ), scene3D.getTerrain3DShift() );
        assertSizes( scene2D, scene3D, 0, INDEX_ZERO_2I, 0, Index3D.id3( 1, 1, 3 ) );
        Assertions.assertNull( scene3D.getTerrains3D()[0][0][0] );
        Assertions.assertNull( scene3D.getTerrains3D()[0][0][1] );
        assertTerrain(
            scene3D.getTerrains3D()[0][0][2],
            Index3D.id3( 0,0, 2 ),
            scene3D.getTerrain3DShift(),
            CubePositionCalculator.computeCubePosition( 3, 3, 5, FirstOracleConstants.INDEX_ZERO_3I ),
            Terrain3DImpl.class,
            "resources/First Oracle/textures/grass.png",
            new float[]{ 1,1,1, 2,2,2, 3,3,3 },
            new float[]{ 1,1, 2,2, 3,3 },
            new float[]{ 1,1,1,1, 2,2,2,2, 3,3,3,3 }
        );
    }
    
    @Test
    void parseSharedData() {
        var scenePair = SceneParser.parseToNonOptimised( `{
            "sharedData": {
                "objectClasses2D": {
                    "obj2d": "PositionableObject2DImpl"
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
                    "tex": "resources/First Oracle/textures/grass.png"
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
                        "positionCalculator": "PlanePositionCalculator"
                    }
                }
            },
            "scene3D": {
                "objects": {
                    "object3": {
                        "class": "$obj3d",
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
        }` );
        var scene2D = scenePair.getScene2D();
        var scene3D = scenePair.getScene3D();
        Assertions.assertEquals( Index2D.id2( 0, 0 ), scene2D.getTerrain2DShift() );
        Assertions.assertEquals( Index3D.id3( 0, 0, 0 ), scene3D.getTerrain3DShift() );
        assertSizes( scene2D, scene3D, 2, Index2D.id2( 2,3 ), 2, Index3D.id3( 1,1,3 ) );
        
        float[] expectedUvMap = { 1, 1, 2, 2, 3, 3 };
        float[] expectedVertices2D = { 1, 1, 0, 2, 2, 0, 3, 3, 0 };
        float[] expectedVertices3D = { 1, 1, 1, 2, 2, 2, 3, 3, 3 };
        float[] expectedColouring = { 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3 };
        var expectedTexturePath = "resources/First Oracle/textures/grass.png";
        
        
        scene2D.getObjects2D().forEach( object -> {
                assertObject(
                    object,
                    pos2( 4, 3 ),
                    rot2( 45 ),
                    scale2( 2, 6 ),
                    PositionableObject2DImpl.class,
                    expectedTexturePath,
                    expectedVertices2D,
                    expectedUvMap,
                    expectedColouring
                );
            }
        );
        
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
            PlanePositionCalculator.computePlanePosition( 1, 2, FirstOracleConstants.INDEX_ZERO_2I ),
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
                PlanePositionCalculator.computePlanePosition( 0, i, FirstOracleConstants.INDEX_ZERO_2I ),
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
                CubePositionCalculator.computeCubePosition( 0, 0, i, FirstOracleConstants.INDEX_ZERO_3I ),
                Terrain3DImpl.class,
                expectedTexturePath,
                expectedVertices3D,
                expectedUvMap,
                expectedColouring
            );
        }
    }
    
    private < IndexT extends Index, PositionT extends Position > void assertTerrain(
        Terrain< ?, ?, ?, IndexT, PositionT > terrain,
        IndexT index,
        IndexT shift,
        PositionT expectedCalculatedPosition,
        Class< ? extends Terrain< ?, ?, ?, ?, ? > > expectedClass,
        String expectedTexturePath,
        float[] expectedVertices,
        float[] expectedUvMap,
        float[] expectedColouring
    ) {
        Assertions.assertNotNull( terrain );
        var data = terrain.getRenderData( terrain.computePosition( index, shift ), 0, 0 ).stream()
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
        PositionableObject< ?, ?, ? > object,
        Position expectedPosition,
        Rotation expectedRotation,
        Scale expectedScale,
        Class< ? extends PositionableObject< ?, ?, ? > > expectedClass,
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
    
    private void assertSizes(
        RegistrableScene2DImpl scene2D,
        RegistrableScene3DImpl scene3D,
        int expectedObjects2dSize,
        Index2D expectedTerrains2dSize,
        int expectedObjects3dSize,
        Index3D expectedTerrains3dSize
    ) {
        var objects2dSize = scene2D.getObjects2D().size();
        var objects3dSize = scene3D.getObjects3D().size();
        var terrains2dSize = arraySize( scene2D.getTerrains2D() );
        var terrains3dSize = arraySize( scene3D.getTerrains3D() );
        
        Assertions.assertTrue(
            Objects.equals( expectedObjects2dSize, objects2dSize ) &&
            Objects.equals( expectedObjects3dSize, objects3dSize ) &&
            Objects.equals( expectedTerrains2dSize, terrains2dSize ) &&
            Objects.equals( expectedTerrains3dSize, terrains3dSize ),
            () ->
                "Expected: " +
                    "2d" + toStr( expectedObjects2dSize, expectedTerrains2dSize ) + ", " +
                    "3d" + toStr( expectedObjects3dSize, expectedTerrains3dSize ) + "\n" +
                "Actual: " +
                    "2d" + toStr( objects2dSize, terrains2dSize ) + ", " +
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