/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.data.Rotation;
import com.firststory.firstoracle.data.Scale;
import com.firststory.firstoracle.object.Colouring;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object.Vertices;

public class RenderData {
    
    public static RenderDataBuilder build( RenderType type ) {
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
    
    public static class RenderDataBuilder {
        
        private final RenderData data = new RenderData();
    
        public RenderData finish() {
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