/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.*;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface ObjectRenderingContext<
    Position,
    Scale,
    Rotation,
    Transformations extends ObjectTransformations< Scale, Rotation >,
    VerticesT extends Vertices<?, ?>,
    GraphicObjectT extends GraphicObject< ? extends Transformations, ?, ? extends VerticesT > >
{
    
    boolean shouldUseTextures();
    
    boolean shouldDrawBorder();
    
    default void render(
        GraphicObjectT object, 
        Position position, 
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
        VerticesT vertices,
        int vertexFrame,
        UvMap uvMap,
        int uvFrame,
        int uvDirection,
        Colouring colours,
        Position position,
        Transformations transformations,
        Texture texture,
        Vector4fc overlayColour,
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
        VerticesT vertices,
        int vertexFrame,
        UvMap uvMap,
        int uvFrame,
        int uvDirection,
        Colouring colours,
        Position position,
        Transformations transformations,
        Texture texture,
        Vector4fc overlayColour,
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
    };
    
    void renderVerticesAsTriangles(
        VerticesT vertices,
        int vertexFrame,
        UvMap uvMap,
        int uvFrame,
        int uvDirection,
        Colouring colours,
        Position position,
        Scale scale,
        Rotation rotation,
        Texture texture,
        Vector4fc overlayColour,
        Float maxAlphaChannel
    );
    
    default void renderVerticesAsLines(
        VerticesT vertices,
        int vertexFrame,
        Position position,
        Transformations transformations,
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
    };
    
    void renderVerticesAsLines(
        VerticesT vertices,
        int vertexFrame,
        Position position,
        Scale scale,
        Rotation rotation,
        LineData lineData
    );
}
