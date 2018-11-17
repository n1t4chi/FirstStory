/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.data.Rotation;
import com.firststory.firstoracle.data.Scale;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.exceptions.ParseFailedException;
import com.firststory.firstoracle.input.structure.Leaf;
import com.firststory.firstoracle.object.PositionableObject;
import com.firststory.firstoracle.object.PositionableObjectTransformations;
import com.firststory.firstoracle.object2D.MutablePositionable2DTransformations;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object3D.MutablePositionable3DTransformations;
import com.firststory.firstoracle.object3D.PositionableObject3D;

import java.util.function.Supplier;

/**
 * @author n1t4chi
 */
public interface TransformationParser {
    
    @SuppressWarnings( "unchecked" )
    static void setTransformations2D(
        PositionableObject2D< ?, ? > object,
        SharedData sharedData,
        Leaf position,
        Leaf rotation,
        Leaf scale
    ) {
        setTransformations(
            object,
            MutablePositionable2DTransformations::new,
            position,
            sharedData.getPosition2DParser(),
            rotation,
            sharedData.getRotation2DParser(),
            scale,
            sharedData.getScale2DParser()
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static void setTransformations3D(
        PositionableObject3D< ?, ?  > object,
        SharedData sharedData,
        Leaf position,
        Leaf rotation,
        Leaf scale
    ) {
        setTransformations(
            object,
            MutablePositionable3DTransformations::new,
            position,
            sharedData.getPosition3DParser(),
            rotation,
            sharedData.getRotation3DParser(),
            scale,
            sharedData.getScale3DParser()
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static <
        PositionType extends Position,
        ScaleType extends Scale,
        RotationType extends Rotation,
        TransformationsType extends PositionableObjectTransformations< PositionType, ScaleType, RotationType >,
        ObjectType extends PositionableObject< PositionType, ScaleType, RotationType, ?, ?, ? >
    > void setTransformations(
        ObjectType object,
        Supplier< TransformationsType > transformationsSupplier,
        Leaf position,
        PositionParser< PositionType > positionParser,
        Leaf rotation,
        RotationParser< RotationType > rotationParser,
        Leaf scale,
        ScaleParser< ScaleType > scaleParser
    ) {
        if ( position == null && rotation == null && scale == null ) {
            return;
        }
        try {
            var mutableObject = ( PositionableObject< ?, ?, ?, TransformationsType, ?, ? > ) object;
            var transformations = mutableObject.getTransformations();
            if ( transformations == null ) {
                mutableObject.setTransformations( transformationsSupplier.get() );
            }
            positionParser.apply( object, position );
            rotationParser.apply( object, rotation );
            scaleParser.apply( object, scale );
            
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object transformations", ex );
        }
    }
}
