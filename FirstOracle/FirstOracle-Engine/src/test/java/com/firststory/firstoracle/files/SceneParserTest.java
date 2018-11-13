/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.PositionableObject;
import com.firststory.firstoracle.object2D.PositionableObject2DImpl;
import com.firststory.firstoracle.object3D.PositionableObject3DImpl;
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
    void sharedObjectData() {
        var scenePair = SceneParser.parseToNonOptimised( `{
            "sharedData": {
                "classes2D": {
                    "impl2d": "PositionableObject2DImpl"
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
                "classes3D": {
                    "impl3d": "PositionableObject3DImpl"
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
                        "class": "$impl2d",
                        "position": "$pos2d",
                        "rotation": "$rot2d",
                        "scale": "$scl2d",
                        "texture": "$tex",
                        "uvMap": "$uv",
                        "vertices": "$vert2d",
                        "colouring": "$col"
                    },
                    "object2": {
                        "class": "$impl2d",
                        "position": "$pos2d",
                        "rotation": "$rot2d",
                        "scale": "$scl2d",
                        "texture": "$tex",
                        "uvMap": "$uv",
                        "vertices": "$vert2d",
                        "colouring": "$col"
                    }
                }
            },
            "scene3D": {
                "objects": {
                    "object3": {
                        "class": "$impl3d",
                        "position": "$pos3d",
                        "rotation": "$rot3d",
                        "scale": "$scl3d",
                        "texture": "$tex",
                        "uvMap": "$uv",
                        "vertices": "$vert3d",
                        "colouring": "$col"
                    },
                    "object4": {
                        "class": "$impl3d",
                        "position": "$pos3d",
                        "rotation": "$rot3d",
                        "scale": "$scl3d",
                        "texture": "$tex",
                        "uvMap": "$uv",
                        "vertices": "$vert3d",
                        "colouring": "$col"
                    }
                }
            }
        }` );
        var scene2D = scenePair.getScene2D();
        var scene3D = scenePair.getScene3D();
        assertSizes( scene2D, scene3D, 2, INDEX_ZERO_2I, 2, INDEX_ZERO_3I );
        
        scene2D.getObjects2D().forEach( object ->
            assertObject(
                object,
                pos2( 4, 3 ),
                rot2( 45 ),
                scale2( 2, 6 ),
                PositionableObject2DImpl.class,
                "resources/First Oracle/textures/grass.png",
                new float[]{ 1,1,0, 2,2,0, 3,3,0 },
                new float[]{ 1,1, 2,2, 3,3 },
                new float[]{ 1,1,1,1, 2,2,2,2, 3,3,3,3 }
            )
        );
        
        scene3D.getObjects3D().forEach( object ->
            assertObject(
                object,
                pos3( 4, 3, 2 ),
                rot3( 10, 20, 30 ),
                scale3( 2, 6, 12 ),
                PositionableObject3DImpl.class,
                "resources/First Oracle/textures/grass.png",
                new float[]{ 1,1,1, 2,2,2, 3,3,3 },
                new float[]{ 1,1, 2,2, 3,3 },
                new float[]{ 1,1,1,1, 2,2,2,2, 3,3,3,3 }
            )
        );
    }
    
    private void assertObject(
        PositionableObject< ?, ?, ? > object2D,
        Position expectedPosition,
        Rotation expectedRotation,
        Scale expectedScale,
        Class< ? extends PositionableObject< ?, ?, ? > > expectedClass,
        String expectedTexturePath,
        float[] expectedVertices,
        float[] expectedUvMap,
        float[] expectedColouring
        ) {
        var data = object2D.getRenderData( 0, 0 ).stream()
            .filter( renderData -> renderData.getType() == RenderType.TRIANGLES )
            .findFirst()
            .orElse( null );
        Assertions.assertNotNull( data );
        Assertions.assertEquals( expectedClass,object2D.getClass() );
        Assertions.assertEquals( expectedPosition,data.getPosition() );
        Assertions.assertEquals( expectedRotation,data.getRotation() );
        Assertions.assertEquals( expectedScale,data.getScale() );
        Assertions.assertEquals( expectedTexturePath,data.getTexture().getData().getName() );
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