/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.parsers.ShareableParser;
import com.firststory.firstoracle.input.parsers.classes.Object2DClassParser;
import com.firststory.firstoracle.input.parsers.classes.Object3DClassParser;
import com.firststory.firstoracle.input.parsers.classes.Terrain2DClassParser;
import com.firststory.firstoracle.input.parsers.classes.Terrain3DClassParser;
import com.firststory.firstoracle.input.parsers.parameters.*;
import com.firststory.firstoracle.input.parsers.parameters.params2D.*;
import com.firststory.firstoracle.input.parsers.parameters.params3D.*;
import com.firststory.firstoracle.input.structure.Composite;

/**
 * @author n1t4chi
 */
public class SharedData {
    private final Object2DClassParser object2DClassParser = new Object2DClassParser();
    private final Terrain2DClassParser terrain2DClassParser = new Terrain2DClassParser();
    private final Position2DCalculatorParser position2DCalculatorParser = new Position2DCalculatorParser();
    private final Vertices2DParser vertices2DParser = new Vertices2DParser();
    private final PositionableTransformations2DParser positionableTransformations2DParser = new PositionableTransformations2DParser();
    private final Transformations2DParser transformations2DParser = new Transformations2DParser();
    private final Position2DParser position2DParser = new Position2DParser();
    private final Scale2DParser scale2DParser = new Scale2DParser();
    private final Rotation2DParser rotation2DParser = new Rotation2DParser();
    
    private final Object3DClassParser object3DClassParser = new Object3DClassParser();
    private final Terrain3DClassParser terrain3DClassParser = new Terrain3DClassParser();
    private final Position3DCalculatorParser position3DCalculatorParser = new Position3DCalculatorParser();
    private final Vertices3DParser vertices3DParser = new Vertices3DParser();
    private final PositionableTransformations3DParser positionableTransformations3DParser = new PositionableTransformations3DParser();
    private final Transformations3DParser transformations3DParser = new Transformations3DParser();
    private final Position3DParser position3DParser = new Position3DParser();
    private final Scale3DParser scale3DParser = new Scale3DParser();
    private final Rotation3DParser rotation3DParser = new Rotation3DParser();
    
    private final UvMapParser uvMapParser = new UvMapParser();
    private final ColouringParser colouringParser = new ColouringParser();
    private final TextureParser textureParser = new TextureParser();
    private final FrameControllerParser frameControllerParser = new FrameControllerParser();
    private final DirectionControllerParser directionControllerParser = new DirectionControllerParser();
    
    
    public SharedData( Composite sharedDataNode ) {
        FirstOracleConstants.executeOnAllFields(
            this,
            field -> field.getName().endsWith( "Parser" ),
            ShareableParser.class,
            obj -> obj.parseShared( sharedDataNode ),
            true
        );
    }
    
    public Vertices2DParser getVertices2DParser() {
        return vertices2DParser;
    }
    
    public Vertices3DParser getVertices3DParser() {
        return vertices3DParser;
    }
    
    public UvMapParser getUvMapParser() {
        return uvMapParser;
    }
    
    public ColouringParser getColouringParser() {
        return colouringParser;
    }
    
    public TextureParser getTextureParser() {
        return textureParser;
    }
    
    public Position2DCalculatorParser getPosition2DCalculatorParser() {
        return position2DCalculatorParser;
    }
    
    public Position3DCalculatorParser getPosition3DCalculatorParser() {
        return position3DCalculatorParser;
    }
    
    public Object2DClassParser getObject2DClassParser() {
        return object2DClassParser;
    }
    
    public Terrain2DClassParser getTerrain2DClassParser() {
        return terrain2DClassParser;
    }
    
    public Object3DClassParser getObject3DClassParser() {
        return object3DClassParser;
    }
    
    public Terrain3DClassParser getTerrain3DClassParser() {
        return terrain3DClassParser;
    }
    
    public Position2DParser getPosition2DParser() {
        return position2DParser;
    }
    
    public Scale2DParser getScale2DParser() {
        return scale2DParser;
    }
    
    public Rotation2DParser getRotation2DParser() {
        return rotation2DParser;
    }
    
    public Position3DParser getPosition3DParser() {
        return position3DParser;
    }
    
    public Scale3DParser getScale3DParser() {
        return scale3DParser;
    }
    
    public Rotation3DParser getRotation3DParser() {
        return rotation3DParser;
    }
    
    public PositionableTransformations2DParser getPositionableTransformations2DParser() {
        return positionableTransformations2DParser;
    }
    
    public Transformations2DParser getTransformations2DParser() {
        return transformations2DParser;
    }
    
    public PositionableTransformations3DParser getPositionableTransformations3DParser() {
        return positionableTransformations3DParser;
    }
    
    public Transformations3DParser getTransformations3DParser() {
        return transformations3DParser;
    }
    
    public FrameControllerParser getFrameControllerParser() {
        return frameControllerParser;
    }
    
    public DirectionControllerParser getDirectionControllerParser() {
        return directionControllerParser;
    }
}
