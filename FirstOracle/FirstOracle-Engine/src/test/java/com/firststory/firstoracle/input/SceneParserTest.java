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
    
    private final MockAttributeLoader mockLoader = new MockAttributeLoader();
    
    @Test
    void parseEmptyScene() {
        var scene2D = SceneParser.parseToNonOptimised2D( "" );
        var scene3D = SceneParser.parseToNonOptimised3D( "" );
        assertSize2D( scene2D,  0, INDEX_ZERO_2I );
        assertSize3D( scene3D, 0, INDEX_ZERO_3I );
    }
    
    @Test
    void parseOneObject2d() {
        var scene2D = SceneParser.parseToNonOptimised2D( "{\n" +
        "    \"scene2D\": {\n" +
        "        \"objects\": {\n" +
        "            \"object1\": {\n" +
        "                \"class\": \"PositionableObject2DImpl\",\n" +
        "                \"transformations\": \"MutablePositionable2DTransformations\",\n" +
        "                \"position\": \"4, 3\",\n" +
        "                \"rotation\": \"45\",\n" +
        "                \"scale\": \"2, 6\",\n" +
        "                \"texture\": \"resources/First Oracle/textures/grass2D.png\",\n" +
        "                \"uvMap\": \"[ [ [ {1,1},  {2,2}, {3,3} ] ] ]\",\n" +
        "                \"vertices\": \"[ [ {1,1}, {2,2}, {3,3} ] ]\",\n" +
        "                \"colouring\": \"[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]\"\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "}\n" );
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
        var scene3D = SceneParser.parseToNonOptimised3D( "{\n" +
        "    \"scene3D\": {\n" +
        "        \"objects\": {\n" +
        "            \"object1\": {\n" +
        "                \"class\": \"PositionableObject3DImpl\",\n" +
        "                \"transformations\": \"MutablePositionable3DTransformations\",\n" +
        "                \"position\": \"4, 3, 2\",\n" +
        "                \"rotation\": \"10, 20, 30\",\n" +
        "                \"scale\": \"2, 6, 12\",\n" +
        "                \"texture\": \"resources/First Oracle/textures/grass2D.png\",\n" +
        "                \"uvMap\": \"[ [ [ {1,1},  {2,2}, {3,3} ] ] ]\",\n" +
        "                \"vertices\": \"[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]\",\n" +
        "                \"colouring\": \"[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]\"\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "}\n" );
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
        var scene2D = SceneParser.parseToNonOptimised2D( "{\n" +
        "    \"configuration\": {\n" +
        "        \"terrain2DShift\": \"{3,3}\",\n" +
        "        \"terrain2DSize\": \"{1,3}\"\n" +
        "    },\n" +
        "    \"scene2D\": {\n" +
        "        \"terrains\": {\n" +
        "            \"object1\": {\n" +
        "                \"class\": \"Terrain2DImpl\",\n" +
        "                \"texture\": \"resources/First Oracle/textures/grass2D.png\",\n" +
        "                \"uvMap\": \"[ [ [ {1,1},  {2,2}, {3,3} ] ] ]\",\n" +
        "                \"vertices\": \"[ [ {1,1}, {2,2}, {3,3} ] ]\",\n" +
        "                \"colouring\": \"[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]\",\n" +
        "                \"indices\": \"[ {0,2} ]\",\n" +
        "                \"positionCalculator\": \"RectanglePositionCalculator\"\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "}\n" );
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
        var scene3D = SceneParser.parseToNonOptimised3D( "{\n" +
        "    \"configuration\": {\n" +
        "        \"terrain3DShift\": \"{3,3,3}\",\n" +
        "        \"terrain3DSize\": \"{1,1,3}\"\n" +
        "    },\n" +
        "    \"scene3D\": {\n" +
        "        \"terrains\": {\n" +
        "            \"object1\": {\n" +
        "                \"class\": \"Terrain3DImpl\",\n" +
        "                \"texture\": \"resources/First Oracle/textures/grass2D.png\",\n" +
        "                \"uvMap\": \"[ [ [ {1,1},  {2,2}, {3,3} ] ] ]\",\n" +
        "                \"vertices\": \"[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]\",\n" +
        "                \"colouring\": \"[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]\",\n" +
        "                \"indices\": \"[ {0,0,2} ]\",\n" +
        "                \"positionCalculator\": \"CubePositionCalculator\"\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "}\n" );
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
        var text = "{\n" +
        "    \"sharedData\": {\n" +
        "        \"objectClasses2D\": {\n" +
        "            \"obj2d\": \"PositionableObject2DImpl\"\n" +
        "        },\n" +
        "        \"positionableTransformations2D\": {\n" +
        "            \"trans2d\": \"MutablePositionable2DTransformations\"\n" +
        "        },\n" +
        "        \"positionableTransformations3D\": {\n" +
        "            \"trans3d\": \"MutablePositionable3DTransformations\"\n" +
        "        },\n" +
        "        \"terrainClasses2D\": {\n" +
        "            \"terr2d\": \"Terrain2DImpl\"\n" +
        "        },\n" +
        "        \"positions2D\": {\n" +
        "            \"pos2d\": \"4, 3\"\n" +
        "        },\n" +
        "        \"rotations2D\": {\n" +
        "            \"rot2d\": \"45\"\n" +
        "        },\n" +
        "        \"scales2D\": {\n" +
        "            \"scl2d\": \"2, 6\"\n" +
        "        },\n" +
        "        \"vertices2D\": {\n" +
        "            \"vert2d\": \"[ [ {1,1}, {2,2}, {3,3} ] ]\"\n" +
        "        },\n" +
        "        \"objectClasses3D\": {\n" +
        "            \"obj3d\": \"PositionableObject3DImpl\"\n" +
        "        },\n" +
        "        \"terrainClasses3D\": {\n" +
        "            \"terr3d\": \"Terrain3DImpl\"\n" +
        "        },\n" +
        "        \"positions3D\": {\n" +
        "            \"pos3d\": \"4, 3, 2\"\n" +
        "        },\n" +
        "        \"rotations3D\": {\n" +
        "            \"rot3d\": \"10, 20, 30\"\n" +
        "        },\n" +
        "        \"scales3D\": {\n" +
        "            \"scl3d\": \"2, 6, 12\"\n" +
        "        },\n" +
        "        \"vertices3D\": {\n" +
        "            \"vert3d\": \"[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]\"\n" +
        "        },\n" +
        "        \"textures\": {\n" +
        "            \"tex\": \"resources/First Oracle/textures/grass2D.png\"\n" +
        "        },\n" +
        "        \"uvMaps\": {\n" +
        "            \"uv\": \"[ [ [ {1,1},  {2,2}, {3,3} ] ] ]\"\n" +
        "        },\n" +
        "        \"colourings\": {\n" +
        "            \"col\": \"[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]\"\n" +
        "        }\n" +
        "    },\n" +
        "    \"scene2D\": {\n" +
        "        \"objects\": {\n" +
        "            \"object1\": {\n" +
        "                \"class\": \"$obj2d\",\n" +
        "                \"transformations\": \"$trans2d\",\n" +
        "                \"position\": \"$pos2d\",\n" +
        "                \"rotation\": \"$rot2d\",\n" +
        "                \"scale\": \"$scl2d\",\n" +
        "                \"texture\": \"$tex\",\n" +
        "                \"uvMap\": \"$uv\",\n" +
        "                \"vertices\": \"$vert2d\",\n" +
        "                \"colouring\": \"$col\"\n" +
        "            },\n" +
        "            \"object2\": {\n" +
        "                \"class\": \"$obj2d\",\n" +
        "                \"transformations\": \"$trans2d\",\n" +
        "                \"position\": \"$pos2d\",\n" +
        "                \"rotation\": \"$rot2d\",\n" +
        "                \"scale\": \"$scl2d\",\n" +
        "                \"texture\": \"$tex\",\n" +
        "                \"uvMap\": \"$uv\",\n" +
        "                \"vertices\": \"$vert2d\",\n" +
        "                \"colouring\": \"$col\"\n" +
        "            }\n" +
        "        },\n" +
        "        \"terrains\": {\n" +
        "            \"terrain1\": {\n" +
        "                \"class\": \"$terr2d\",\n" +
        "                \"texture\": \"$tex\",\n" +
        "                \"uvMap\": \"$uv\",\n" +
        "                \"vertices\": \"$vert2d\",\n" +
        "                \"colouring\": \"$col\",\n" +
        "                \"indices\": \"[ {0,0}x{0,2}, {1,2} ]\",\n" +
        "                \"positionCalculator\": \"RectanglePositionCalculator\"\n" +
        "            }\n" +
        "        }\n" +
        "    },\n" +
        "    \"scene3D\": {\n" +
        "        \"objects\": {\n" +
        "            \"object3\": {\n" +
        "                \"class\": \"$obj3d\",\n" +
        "                \"transformations\": \"$trans3d\",\n" +
        "                \"position\": \"$pos3d\",\n" +
        "                \"rotation\": \"$rot3d\",\n" +
        "                \"scale\": \"$scl3d\",\n" +
        "                \"texture\": \"$tex\",\n" +
        "                \"uvMap\": \"$uv\",\n" +
        "                \"vertices\": \"$vert3d\",\n" +
        "                \"colouring\": \"$col\"\n" +
        "            },\n" +
        "            \"object4\": {\n" +
        "                \"class\": \"$obj3d\",\n" +
        "                \"transformations\": \"$trans3d\",\n" +
        "                \"position\": \"$pos3d\",\n" +
        "                \"rotation\": \"$rot3d\",\n" +
        "                \"scale\": \"$scl3d\",\n" +
        "                \"texture\": \"$tex\",\n" +
        "                \"uvMap\": \"$uv\",\n" +
        "                \"vertices\": \"$vert3d\",\n" +
        "                \"colouring\": \"$col\"\n" +
        "            }\n" +
        "        },\n" +
        "        \"terrains\": {\n" +
        "            \"terrain2\": {\n" +
        "                \"class\": \"$terr3d\",\n" +
        "                \"texture\": \"$tex\",\n" +
        "                \"uvMap\": \"$uv\",\n" +
        "                \"vertices\": \"$vert3d\",\n" +
        "                \"colouring\": \"$col\",\n" +
        "                \"indices\": \"[ {0,0,0}x{0,0,2} ]\",\n" +
        "                \"positionCalculator\": \"CubePositionCalculator\"\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "}\n";
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
        var text = "{\n" +
        "    \"sharedObjects\": {\n" +
        "        \"objects2D\": {\n" +
        "            \"baseObj2d\": {\n" +
        "                \"class\": \"PositionableObject2DImpl\",\n" +
        "                \"transformations\": \"MutablePositionable2DTransformations\",\n" +
        "                \"rotation\": \"45\",\n" +
        "                \"scale\": \"2, 6\",\n" +
        "                \"texture\": \"resources/First Oracle/textures/grass2D.png\",\n" +
        "                \"uvMap\": \"[ [ [ {1,1},  {2,2}, {3,3} ] ] ]\",\n" +
        "                \"vertices\": \"[ [ {1,1}, {2,2}, {3,3} ] ]\",\n" +
        "                \"colouring\": \"[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]\"\n" +
        "            }\n" +
        "        },\n" +
        "        \"terrains2D\": {\n" +
        "            \"baseTer2d\": {\n" +
        "                \"class\": \"Terrain2DImpl\",\n" +
        "                \"texture\": \"resources/First Oracle/textures/grass2D.png\",\n" +
        "                \"uvMap\": \"[ [ [ {1,1},  {2,2}, {3,3} ] ] ]\",\n" +
        "                \"vertices\": \"[ [ {1,1}, {2,2}, {3,3} ] ]\",\n" +
        "                \"colouring\": \"[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]\",\n" +
        "                \"positionCalculator\": \"RectanglePositionCalculator\"\n" +
        "            }\n" +
        "        },\n" +
        "        \"objects3D\": {\n" +
        "            \"baseObj3d\": {\n" +
        "                \"class\": \"PositionableObject3DImpl\",\n" +
        "                \"transformations\": \"MutablePositionable3DTransformations\",\n" +
        "                \"rotation\": \"10, 20, 30\",\n" +
        "                \"scale\": \"2, 6, 12\",\n" +
        "                \"texture\": \"resources/First Oracle/textures/grass2D.png\",\n" +
        "                \"uvMap\": \"[ [ [ {1,1},  {2,2}, {3,3} ] ] ]\",\n" +
        "                \"vertices\": \"[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]\",\n" +
        "                \"colouring\": \"[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]\"\n" +
        "            }\n" +
        "        },\n" +
        "        \"terrains3D\": {\n" +
        "            \"baseTer3d\": {\n" +
        "                \"class\": \"Terrain3DImpl\",\n" +
        "                \"texture\": \"resources/First Oracle/textures/grass2D.png\",\n" +
        "                \"uvMap\": \"[ [ [ {1,1},  {2,2}, {3,3} ] ] ]\",\n" +
        "                \"vertices\": \"[ [ {1,1,1}, {2,2,2}, {3,3,3} ] ]\",\n" +
        "                \"colouring\": \"[ {1,1,1,1}, {2,2,2,2}, {3,3,3,3} ]\",\n" +
        "                \"positionCalculator\": \"CubePositionCalculator\"\n" +
        "            }\n" +
        "        }\n" +
        "    },\n" +
        "    \"scene2D\": {\n" +
        "        \"objects\": {\n" +
        "            \"object1\": {\n" +
        "                \"base\": \"$baseObj2d\",\n" +
        "                \"positions\": \"[{1,1},{2,2}]\"\n" +
        "            }\n" +
        "        },\n" +
        "        \"terrains\": {\n" +
        "            \"terrain1\": {\n" +
        "                \"base\": \"$baseTer2d\",\n" +
        "                \"indices\": \"[ {0,0}x{2,2} ]\"\n" +
        "            }\n" +
        "        }\n" +
        "    },\n" +
        "    \"scene3D\": {\n" +
        "        \"objects\": {\n" +
        "            \"object3\": {\n" +
        "                \"base\": \"$baseObj3d\",\n" +
        "                \"positions\": \"[{1,1,1},{2,2,2}]\"\n" +
        "            }\n" +
        "        },\n" +
        "        \"terrains\": {\n" +
        "            \"terrain2\": {\n" +
        "                \"base\": \"$baseTer3d\",\n" +
        "                \"indices\": \"[ {0,0,0}x{2,2,2} ]\"\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "}\n";
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
