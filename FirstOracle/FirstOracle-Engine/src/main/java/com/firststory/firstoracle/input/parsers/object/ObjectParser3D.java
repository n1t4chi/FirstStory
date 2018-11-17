/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.parsers.classes.Object3DClassParser;
import com.firststory.firstoracle.input.parsers.classes.Terrain3DClassParser;
import com.firststory.firstoracle.input.parsers.parameters.TransformationParser;
import com.firststory.firstoracle.input.parsers.parameters.VerticesParser;
import com.firststory.firstoracle.input.structure.Leaf;
import com.firststory.firstoracle.object3D.*;

import java.util.List;

/**
 * @author n1t4chi
 */
public class ObjectParser3D extends ObjectParser<
    PositionableObject3D< ?, ? >,
    Terrain3D< ? >,
    Vertices3D,
    Position3D,
    Index3D,
    Object3DClassParser,
    Terrain3DClassParser
> {
    
    @Override
    void setTransformation(
        PositionableObject3D< ?, ? > object,
        SharedData sharedData,
        Leaf position,
        Leaf rotation,
        Leaf scale
    ) {
        TransformationParser.setTransformations3D(
            object,
            sharedData,
            position,
            rotation,
            scale
        );
    }
    
    @Override
    VerticesParser< Vertices3D, Position3D > getVerticesParser( SharedData sharedData ) {
        return sharedData.getVertices3DParser();
    }
    
    @Override
    void setPositionCalculator(
        Terrain3D< ? > terrain,
        SharedData sharedData,
        Leaf leaf
    ) {
        sharedData.getPosition3DCalculatorParser().apply(
            terrain,
            leaf
        );
    }
    
    public Terrain3DClassParser getTerrainClassParser(
        SharedData sharedData
    ) {
        return sharedData.getTerrain3DClassParser();
    }
    
    public Object3DClassParser getObjectClassParser(
        SharedData sharedData
    ) {
        return sharedData.getObject3DClassParser();
    }
    
    @Override
    List< Index3D > parseIndex( String text ) {
        return ParseUtils.toIndices3D( text );
    }
    
    @Override
    Class< PositionableObject3DImpl > getDefaultObjectClass() {
        return PositionableObject3DImpl.class;
    }
    
    @Override
    Class< Terrain3DImpl > getDefaultTerrainClass() {
        return Terrain3DImpl.class;
    }
}
