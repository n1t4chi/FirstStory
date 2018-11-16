/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.ParseUtils.TriConsumer;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.exceptions.ParseFailedException;
import com.firststory.firstoracle.object.PositionableObject;
import com.firststory.firstoracle.object.PositionableObjectTransformations;
import com.firststory.firstoracle.object2D.Mutable2DTransformations;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object3D.Mutable3DTransformations;
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
        String position,
        String rotation,
        String scale
    ) {
        setTransformations(
            object,
            sharedData,
            Mutable2DTransformations::new,
            position,
            TransformationParser::setPosition2D,
            rotation,
            TransformationParser::setRotation2D,
            scale,
            TransformationParser::setScale2D
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static void setTransformations3D(
        PositionableObject3D< ?, ? > object,
        SharedData sharedData,
        String position,
        String rotation,
        String scale
    ) {
        setTransformations(
            object,
            sharedData,
            Mutable3DTransformations::new,
            position,
            TransformationParser::setPosition3D,
            rotation,
            TransformationParser::setRotation3D,
            scale,
            TransformationParser::setScale3D
        );
    }
    
    @SuppressWarnings( "unchecked" )
    static < O extends PositionableObject<?,?,?>, T extends PositionableObjectTransformations<?,?,?> > void setTransformations(
        O object,
        SharedData sharedData,
        Supplier< T > transformationsSupplier,
        String position,
        TriConsumer< SharedData, String, T > positionApplier,
        String rotation,
        TriConsumer< SharedData, String, T > rotationApplier,
        String scale,
        TriConsumer< SharedData, String, T > scaleApplier
    ) {
        if ( position == null && rotation == null && scale == null ) {
            return;
        }
        try {
            var mutableObject = ( PositionableObject<T,?,?> ) object;
            var transformations = mutableObject.getTransformations();
            if ( transformations == null ) {
                mutableObject.setTransformations( transformations = transformationsSupplier.get() );
            }
            updateTransformations(
                transformations,
                sharedData,
                position,
                positionApplier,
                rotation,
                rotationApplier,
                scale,
                scaleApplier
            );
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object transformations", ex );
        }
    }
    
    static < T extends PositionableObjectTransformations<?,?,?> > T updateTransformations(
        T transformations,
        SharedData sharedData,
        String position,
        TriConsumer< SharedData, String, T > positionApplier,
        String rotation,
        TriConsumer< SharedData, String, T > rotationApplier,
        String scale,
        TriConsumer< SharedData, String, T > scaleApplier
    ) {
        try {
            if ( position != null ) {
                positionApplier.accept( sharedData, position, transformations );
            }
            if ( rotation != null ) {
                rotationApplier.accept( sharedData, rotation, transformations );
            }
            if ( scale != null ) {
                scaleApplier.accept( sharedData, scale, transformations );
            }
            return transformations;
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object transformations", ex );
        }
    }
    
    private static void setScale2D(
        SharedData sharedData,
        String scale,
        Mutable2DTransformations transformations
    ) {
        transformations.setScale( newScale2D( sharedData, scale ) );
    }
    
    private static void setRotation2D(
        SharedData sharedData,
        String rotation,
        Mutable2DTransformations transformations
    ) {
        transformations.setRotation( newRotation2D( sharedData, rotation ) );
    }
    
    private static void setPosition2D(
        SharedData sharedData,
        String position,
        Mutable2DTransformations transformations
    ) {
        transformations.setPosition( newPosition2D( sharedData, position ) );
    }
    
    private static void setScale3D(
        SharedData sharedData,
        String scale,
        Mutable3DTransformations transformations
    ) {
        transformations.setScale( newScale3D( sharedData, scale ) );
    }
    
    private static void setRotation3D(
        SharedData sharedData,
        String rotation,
        Mutable3DTransformations transformations
    ) {
        transformations.setRotation( newRotation3D( sharedData, rotation ) );
    }
    
    private static void setPosition3D(
        SharedData sharedData,
        String position,
        Mutable3DTransformations transformations
    ) {
        transformations.setPosition( newPosition3D( sharedData, position ) );
    }
    
    
    private static Scale2D newScale2D(
        SharedData sharedData,
        String scale
    ) {
        return ParseUtils.getNewOrShared(
            scale,
            sharedData,
            SharedData::getScale2D,
            ParseUtils::toScale2D
        );
    }
    
    private static Rotation2D newRotation2D(
        SharedData sharedData,
        String rotation
    ) {
        return ParseUtils.getNewOrShared(
            rotation,
            sharedData,
            SharedData::getRotation2D,
            ParseUtils::toRotation2D
        );
    }
    
    private static Position2D newPosition2D(
        SharedData sharedData,
        String position
    ) {
        return ParseUtils.getNewOrShared(
            position,
            sharedData,
            SharedData::getPosition2D,
            ParseUtils::toPosition2D
        );
    }
    
    private static Scale3D newScale3D(
        SharedData sharedData,
        String scale
    ) {
        return ParseUtils.getNewOrShared(
            scale,
            sharedData,
            SharedData::getScale3D,
            ParseUtils::toScale3D
        );
    }
    
    private static Rotation3D newRotation3D(
        SharedData sharedData,
        String rotation
    ) {
        return ParseUtils.getNewOrShared(
            rotation,
            sharedData,
            SharedData::getRotation3D,
            ParseUtils::toRotation3D
        );
    }
    
    private static Position3D newPosition3D(
        SharedData sharedData,
        String position
    ) {
        return ParseUtils.getNewOrShared(
            position,
            sharedData,
            SharedData::getPosition3D,
            ParseUtils::toPosition3D
        );
    }
}
