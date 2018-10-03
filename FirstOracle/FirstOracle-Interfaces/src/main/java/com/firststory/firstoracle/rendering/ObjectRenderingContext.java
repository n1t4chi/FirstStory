/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.*;

import java.util.List;

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
    
    default void renderObject(
        GraphicObjectType object,
        PositionType position,
        double timeSnapshot,
        double cameraRotation
    ) {
        renderObject(
            object.getVertices(),
            object.getCurrentVertexFrame( timeSnapshot ),
            object.getUvMap(),
            object.getCurrentUvMapFrame( timeSnapshot ),
            object.getCurrentUvMapDirection( cameraRotation ),
            object.getColouring(),
            position,
            object.getTransformations(),
            object.getTexture(),
            object.getOverlayColour(),
            object.getMaxAlphaChannel(),
            object.getLineLoop()
        );
    }

    default void renderObject(
        VerticesType vertices,
        int vertexFrame,
        UvMap uvMap,
        int uvFrame,
        int uvDirection,
        Colouring colouring,
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
            colouring,
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
        Colouring colouring,
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
            colouring,
            position,
            transformations.getScale(),
            transformations.getRotation(),
            texture,
            overlayColour,
            maxAlphaChannel
        );
    }

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

    default void renderVerticesAsTriangles(
        VerticesType vertices,
        int vertexFrame,
        UvMap uvMap,
        int uvFrame,
        int uvDirection,
        Colouring colouring,
        PositionType position,
        ScaleType scale,
        RotationType rotation,
        Texture texture,
        Colour overlayColour,
        Float maxAlphaChannel
    ) {
        render( RenderData.renderData( RenderType.TRIANGLES )
            .setPosition( position )
            .setRotation( rotation )
            .setScale( scale )
            .setMaxAlphaChannel( maxAlphaChannel )
            .setOverlayColour( overlayColour )
            .setVertices( vertices )
            .setVertexFrame( vertexFrame )
            .setTexture( texture )
            .setUvMap( uvMap )
            .setUvDirection( uvDirection )
            .setUvFrame( uvFrame )
            .setColouring( colouring )
            .build()
        );
    }

    default void renderVerticesAsLines(
        VerticesType vertices,
        int vertexFrame,
        PositionType position,
        ScaleType scale,
        RotationType rotation,
        LineData lineData
    ) {
        render( RenderData.renderData( lineData.getType().getRenderType() )
            .setPosition( position )
            .setRotation( rotation )
            .setScale( scale )
            .setOverlayColour( lineData.getColour() )
            .setVertices( vertices )
            .setVertexFrame( vertexFrame )
            .setLineWidth( lineData.getWidth() )
            .build()
        );
    }
    
    default void render( List< RenderData > renderData ) {
        renderData.forEach( this::render );
    }
    
    void render( RenderData renderData );
}
