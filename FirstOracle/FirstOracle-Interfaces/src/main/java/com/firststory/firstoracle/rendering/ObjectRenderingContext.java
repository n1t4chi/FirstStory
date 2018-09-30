/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.object.data.Colour;
import com.firststory.firstoracle.object.data.Position;
import com.firststory.firstoracle.object.data.Rotation;
import com.firststory.firstoracle.object.data.Scale;

/**
 * @author n1t4chi
 */
public interface ObjectRenderingContext<
    PositionType extends Position,
    ScaleType extends Scale,
    RotationType extends Rotation,
    TransformationsType extends ObjectTransformations< ScaleType, RotationType >,
    VerticesType extends Vertices<?, ?>,
    GraphicObjectType extends GraphicObject< ? extends TransformationsType, ?, ? extends VerticesType > >
{
    
    boolean shouldUseTextures();
    
    boolean shouldDrawBorder();
    
    default void render(
        GraphicObjectType object,
        PositionType position,
        double timeSnapshot, 
        double cameraRotation
    ) {
        render(
            object.getVertices(),
            object.getCurrentVertexFrame( timeSnapshot ),
            object.getUvMap(),
            object.getCurrentUvMapFrame( timeSnapshot ),
            object.getCurrentUvMapDirection( cameraRotation ),
            object.getColours(),
            position,
            object.getTransformations(),
            object.getTexture(),
            object.getOverlayColour(),
            object.getMaxAlphaChannel(),
            object.getLineLoop()
        );
    }
    
    default void render(
        VerticesType vertices,
        int vertexFrame,
        UvMap uvMap,
        int uvFrame,
        int uvDirection,
        Colouring colours,
        PositionType position,
        TransformationsType transformations,
        Texture texture,
        Colour overlayColour,
        Float maxAlphaChannel,
        LineData lineLoop
    ) {
        if( !shouldUseTextures() ) {
            uvMap = FirstOracleConstants.EMPTY_UV_MAP;
            uvFrame =  uvDirection = 0;
            texture = FirstOracleConstants.EMPTY_TEXTURE;
        }
        renderVerticesAsTriangles(
            vertices,
            vertexFrame,
            uvMap,
            uvFrame,
            uvDirection,
            colours,
            position,
            transformations,
            texture,
            overlayColour,
            maxAlphaChannel
        );
        if( shouldDrawBorder() ) {
            renderVerticesAsLines(
                vertices,
                vertexFrame,
                position,
                transformations,
                lineLoop
            );
        }
    }
    
    
    default void renderVerticesAsTriangles(
        VerticesType vertices,
        int vertexFrame,
        UvMap uvMap,
        int uvFrame,
        int uvDirection,
        Colouring colours,
        PositionType position,
        TransformationsType transformations,
        Texture texture,
        Colour overlayColour,
        Float maxAlphaChannel
    ) {
        renderVerticesAsTriangles(
            vertices,
            vertexFrame,
            uvMap,
            uvFrame,
            uvDirection,
            colours,
            position,
            transformations.getScale(),
            transformations.getRotation(),
            texture,
            overlayColour,
            maxAlphaChannel
        );
    }
    
    void renderVerticesAsTriangles(
        VerticesType vertices,
        int vertexFrame,
        UvMap uvMap,
        int uvFrame,
        int uvDirection,
        Colouring colours,
        PositionType position,
        ScaleType scale,
        RotationType rotation,
        Texture texture,
        Colour overlayColour,
        Float maxAlphaChannel
    );
    
    default void renderVerticesAsLines(
        VerticesType vertices,
        int vertexFrame,
        PositionType position,
        TransformationsType transformations,
        LineData lineData
    ) {
        renderVerticesAsLines(
            vertices,
            vertexFrame,
            position,
            transformations.getScale(),
            transformations.getRotation(),
            lineData
        );
    }
    
    void renderVerticesAsLines(
        VerticesType vertices,
        int vertexFrame,
        PositionType position,
        ScaleType scale,
        RotationType rotation,
        LineData lineData
    );
}
