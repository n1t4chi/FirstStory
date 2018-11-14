/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.*;

public class RenderData {
    private static final LineData DEFAULT_BORDER_COLOUR = FirstOracleConstants.RED_LINE_LOOP;
    
    /**
     * Returns builder which returns can return unique Render Data.
     * Same Render Data is returned each time {@link RenderDataBuilder#build()} is called.
     * Builder is initialised for given positionable object, aside from dynamic data like vertex frame
     * @param object object
     */
    public static RenderDataBuilder borderRenderDataForObject(
        PositionableObject< ?, ?, ? > object
    ) {
        return borderRenderData( object, object.getTransformations().getPosition() );
    }
    
    /**
     * Returns builder which returns can return unique Render Data.
     * Same Render Data is returned each time {@link RenderDataBuilder#build()} is called.
     * Builder is initialised for given terrain, aside from dynamic data like vertex frame
     * @param terrain terrain
     * @param position terrain position
     */
    public static RenderDataBuilder borderRenderDataForTerrain(
        Terrain< ?, ?, ?, ?, ? > terrain,
        Position position
    ) {
        return borderRenderData( terrain, position );
    }
    
    /**
     * Returns builder which returns can return unique Render Data.
     * Same Render Data is returned each time {@link RenderDataBuilder#build()} is called.
     * Builder is initialised for given positionable object, aside from dynamic data like vertex frame
     * @param object object
     */
    public static RenderDataBuilder baseRenderDataForObject(
        PositionableObject< ?, ?, ? > object
    ) {
        return baseRenderData( object, object.getTransformations().getPosition() );
    }
    
    /**
     * Returns builder which returns can return unique Render Data.
     * Same Render Data is returned each time {@link RenderDataBuilder#build()} is called.
     * Builder is initialised for given terrain, aside from dynamic data like vertex frame
     * @param terrain terrain
     * @param position terrain position
     */
    public static RenderDataBuilder baseRenderDataForTerrain(
        Terrain< ?, ?, ?, ?, ? > terrain,
        Position position
    ) {
        return baseRenderData( terrain, position );
    }
    
    /**
     * Returns builder which returns can return unique Render Data.
     * Same Render Data is returned each time {@link RenderDataBuilder#build()} is called.
     * Builder is initialised for base rendering of object, aside from dynamic data like vertex frame
     * @param object object
     * @param position object position
     */
    public static RenderDataBuilder baseRenderData(
        GraphicObject< ?, ?, ? > object,
        Position position
    ) {
        return renderData( RenderType.TRIANGLES )
            .setPosition( position )
            .setRotation( object.getTransformations().getRotation() )
            .setScale( object.getTransformations().getScale() )
            .setMaxAlphaChannel( object.getMaxAlphaChannel() )
            .setOverlayColour( object.getOverlayColour() )
            .setVertices( object.getVertices() )
            .setTexture( object.getTexture() )
            .setUvMap( object.getUvMap() )
            .setColouring( object.getColouring() )
        ;
    }
    
    /**
     * Returns builder which returns can return unique Render Data.
     * Same Render Data is returned each time {@link RenderDataBuilder#build()} is called.
     * Builder is fully initialised for border rendering of object, aside from dynamic data like vertex frame
     * @param object object
     * @param position object position
     */
    public static RenderDataBuilder borderRenderData(
        GraphicObject< ?, ?, ? > object,
        Position position
    ) {
        return renderData( RenderType.BORDER )
            .setPosition( position )
            .setRotation( object.getTransformations().getRotation() )
            .setScale( object.getTransformations().getScale() )
            .setOverlayColour( DEFAULT_BORDER_COLOUR.getColour() )
            .setLineWidth( DEFAULT_BORDER_COLOUR.getWidth() )
            .setUvMap( object.getUvMap() )
            .setVertices( object.getVertices() )
        ;
    }
    
    /**
     * Returns builder which returns can return unique Render Data.
     * Same Render Data is returned each time {@link RenderDataBuilder#build()} is called.
     * Builder is fully initialised for rendering of line objects
     * @param vertices vertices
     * @param vertexFrame vertexFrame
     * @param position object position
     * @param scale scale
     * @param rotation rotation
     * @param lineData lineData
     */
    public static RenderDataBuilder renderLineData(
        Vertices< ?, ? > vertices,
        int vertexFrame,
        Position position,
        Scale scale,
        Rotation rotation,
        LineData lineData
    ) {
        return renderData( lineData.getType().getRenderType() )
            .setPosition( position )
            .setRotation( rotation )
            .setScale( scale )
            .setOverlayColour( lineData.getColour() )
            .setVertices( vertices )
            .setVertexFrame( vertexFrame )
            .setLineWidth( lineData.getWidth() )
        ;
    }
    
    /**
     * Returns builder which returns can return unique Render Data.
     * Same Render Data is returned each time {@link RenderDataBuilder#build()} is called.
     * @param type render type
     */
    public static RenderDataBuilder renderData( RenderType type ) {
        return new RenderDataBuilder().setRenderType( type );
    }
    
    private Vertices< ?, ? > vertices = null;
    private int vertexFrame = 0;
    private UvMap uvMap = FirstOracleConstants.EMPTY_UV_MAP;
    private int uvFrame = 0;
    private int uvDirection = 0;
    private Colouring colouring = FirstOracleConstants.EMPTY_COLOURING;
    private Position position = FirstOracleConstants.POSITION_ZERO_3F;
    private Scale scale = FirstOracleConstants.SCALE_ONE_3F;
    private Rotation rotation = FirstOracleConstants.ROTATION_ZERO_3F;
    private Texture texture = FirstOracleConstants.EMPTY_TEXTURE;
    private Colour overlayColour = FirstOracleConstants.TRANSPARENT;
    private Float maxAlphaChannel = 1f;
    private Float lineWidth = 1f;
    private RenderType type = RenderType.TRIANGLES;
    
    public RenderType getType() {
        return type;
    }
    
    public Vertices< ?, ? > getVertices() {
        return vertices;
    }
    
    public int getVertexFrame() {
        return vertexFrame;
    }
    
    public UvMap getUvMap() {
        return uvMap;
    }
    
    public int getUvFrame() {
        return uvFrame;
    }
    
    public int getUvDirection() {
        return uvDirection;
    }
    
    public Colouring getColouring() {
        return colouring;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public Scale getScale() {
        return scale;
    }
    
    public Rotation getRotation() {
        return rotation;
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    public Colour getOverlayColour() {
        return overlayColour;
    }
    
    public Float getMaxAlphaChannel() {
        return maxAlphaChannel;
    }
    
    public Float getLineWidth() {
        return lineWidth;
    }
    
    /**
     * Each instance of RenderDataBuilder will return unique RenderData each time {@link #build()}
     * is returned to provide mutable render data for creator but immutable for user
     */
    public static class RenderDataBuilder {
        
        private final RenderData data = new RenderData();
    
        /**
         * Returns unique render data, returns same instance on new calls with updated values
         * @return render data
         */
        public RenderData build() {
            return data;
        }
        
        private RenderDataBuilder setRenderType( RenderType type ) {
            data.type = type;
            return this;
        }
    
        public RenderDataBuilder setVertices( Vertices< ?, ? > vertices ) {
            data.vertices = vertices;
            return this;
        }
        
        public RenderDataBuilder setVertexFrame( int vertexFrame ) {
            data.vertexFrame = vertexFrame;
            return this;
        }
        
        public RenderDataBuilder setUvMap( UvMap uvMap ) {
            data.uvMap = uvMap;
            return this;
        }
        
        public RenderDataBuilder setUvFrame( int uvFrame ) {
            data.uvFrame = uvFrame;
            return this;
        }
        
        public RenderDataBuilder setUvDirection( int uvDirection ) {
            data.uvDirection = uvDirection;
            return this;
        }
        
        public RenderDataBuilder setColouring( Colouring colours ) {
            data.colouring = colours;
            return this;
        }
        
        public RenderDataBuilder setPosition( Position position ) {
            data.position = position;
            return this;
        }
        
        public RenderDataBuilder setScale( Scale scale ) {
            data.scale = scale;
            return this;
        }
        
        public RenderDataBuilder setRotation( Rotation rotation ) {
            data.rotation = rotation;
            return this;
        }
        
        public RenderDataBuilder setTexture( Texture texture ) {
            data.texture = texture;
            return this;
        }
        
        public RenderDataBuilder setOverlayColour( Colour overlayColour ) {
            data.overlayColour = overlayColour;
            return this;
        }
        
        public RenderDataBuilder setMaxAlphaChannel( Float maxAlphaChannel ) {
            data.maxAlphaChannel = maxAlphaChannel;
            return this;
        }
        
        public RenderDataBuilder setLineWidth( Float lineWidth ) {
            data.lineWidth = lineWidth;
            return this;
        }
    }
}