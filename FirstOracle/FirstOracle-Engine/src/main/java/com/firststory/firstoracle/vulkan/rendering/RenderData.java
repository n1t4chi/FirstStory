/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.Colouring;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object.Vertices;
import com.firststory.firstoracle.object.data.Colour;
import com.firststory.firstoracle.object.data.Position;
import com.firststory.firstoracle.object.data.Rotation;
import com.firststory.firstoracle.object.data.Scale;
import com.firststory.firstoracle.object2D.Vertices2D;
import com.firststory.firstoracle.object3D.Vertices3D;
import com.firststory.firstoracle.rendering.RenderType;

public class RenderData {
    
    static RenderDataBuilder build( RenderType type ) {
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
    
    RenderType getType() {
        return type;
    }
    
    Vertices< ?, ? > getVertices() {
        return vertices;
    }
    
    int getVertexFrame() {
        return vertexFrame;
    }
    
    UvMap getUvMap() {
        return uvMap;
    }
    
    int getUvFrame() {
        return uvFrame;
    }
    
    int getUvDirection() {
        return uvDirection;
    }
    
    Colouring getColouring() {
        return colouring;
    }
    
    Position getPosition() {
        return position;
    }
    
    Scale getScale() {
        return scale;
    }
    
    Rotation getRotation() {
        return rotation;
    }
    
    Texture getTexture() {
        return texture;
    }
    
    Colour getOverlayColour() {
        return overlayColour;
    }
    
    Float getMaxAlphaChannel() {
        return maxAlphaChannel;
    }
    
    Float getLineWidth() {
        return lineWidth;
    }
    
    static class RenderDataBuilder {
        
        final RenderData data = new RenderData();
    
        RenderData finish() {
            return data;
        }
        
        private RenderDataBuilder setRenderType( RenderType type ) {
            data.type = type;
            return this;
        }
    
        RenderDataBuilder setVertices( Vertices2D vertices ) {
            data.vertices = vertices;
            return this;
        }
        
        RenderDataBuilder setVertices( Vertices3D vertices ) {
            data.vertices = vertices;
            return this;
        }
        
        RenderDataBuilder setVertexFrame( int vertexFrame ) {
            data.vertexFrame = vertexFrame;
            return this;
        }
        
        RenderDataBuilder setUvMap( UvMap uvMap ) {
            data.uvMap = uvMap;
            return this;
        }
        
        RenderDataBuilder setUvFrame( int uvFrame ) {
            data.uvFrame = uvFrame;
            return this;
        }
        
        RenderDataBuilder setUvDirection( int uvDirection ) {
            data.uvDirection = uvDirection;
            return this;
        }
        
        RenderDataBuilder setColouring( Colouring colours ) {
            data.colouring = colours;
            return this;
        }
        
        RenderDataBuilder setPosition( Position position ) {
            data.position = position;
            return this;
        }
        
        RenderDataBuilder setScale( Scale scale ) {
            data.scale = scale;
            return this;
        }
        
        RenderDataBuilder setRotation( Rotation rotation ) {
            data.rotation = rotation;
            return this;
        }
        
        RenderDataBuilder setTexture( Texture texture ) {
            data.texture = texture;
            return this;
        }
        
        RenderDataBuilder setOverlayColour( Colour overlayColour ) {
            data.overlayColour = overlayColour;
            return this;
        }
        
        RenderDataBuilder setMaxAlphaChannel( Float maxAlphaChannel ) {
            data.maxAlphaChannel = maxAlphaChannel;
            return this;
        }
        
        RenderDataBuilder setLineWidth( Float lineWidth ) {
            data.lineWidth = lineWidth;
            return this;
        }
    }
}