/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.Colour;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object.Vertices;
import com.firststory.firstoracle.object2D.Vertices2D;
import com.firststory.firstoracle.object3D.Vertices3D;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

class RenderData {
    
    static RenderDataBuilder build( RenderType type ) {
        return new RenderDataBuilder().setRenderType( type );
    }
    
    private Vertices vertices = null;
    private int vertexFrame = 0;
    private UvMap uvMap = FirstOracleConstants.EMPTY_UV_MAP;
    private int uvFrame = 0;
    private int uvDirection = 0;
    private Colour colours = FirstOracleConstants.EMPTY_COLOUR;
    private Vector3fc position = FirstOracleConstants.VECTOR_ZERO_3F;
    private Vector3fc scale = FirstOracleConstants.VECTOR_ONES_3F;
    private Vector3fc rotation = FirstOracleConstants.VECTOR_ZERO_3F;
    private Texture texture = FirstOracleConstants.EMPTY_TEXTURE;
    private Vector4fc overlayColour = FirstOracleConstants.TRANSPARENT;
    private Float maxAlphaChannel = 1f;
    private Float lineWidth = 1f;
    private RenderType type = RenderType.TRIANGLES;
    
    RenderType getType() {
        return type;
    }
    
    Vertices getVertices() {
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
    
    Colour getColours() {
        return colours;
    }
    
    Vector3fc getPosition() {
        return position;
    }
    
    Vector3fc getScale() {
        return scale;
    }
    
    Vector3fc getRotation() {
        return rotation;
    }
    
    Texture getTexture() {
        return texture;
    }
    
    Vector4fc getOverlayColour() {
        return overlayColour;
    }
    
    Float getMaxAlphaChannel() {
        return maxAlphaChannel;
    }
    
    Float getLineWidth() {
        return lineWidth;
    }
    
    static class RenderDataBuilder {
        
        RenderData data = new RenderData();
    
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
        
        RenderDataBuilder setColours( Colour colours ) {
            data.colours = colours;
            return this;
        }
        
        RenderDataBuilder setPosition( Vector3fc position ) {
            data.position = new Vector3f( position );
            return this;
        }
        
        RenderDataBuilder setPosition( Vector2fc position ) {
            data.position = new Vector3f( position.x(), position.y(), 0 );
            return this;
        }
        
        RenderDataBuilder setScale( Vector3fc scale ) {
            data.scale = new Vector3f( scale );
            return this;
        }
        
        RenderDataBuilder setScale( Vector2fc scale ) {
            data.scale = new Vector3f( scale.x(), scale.y(), 1 );
            return this;
        }
        
        RenderDataBuilder setRotation( Vector3fc rotation ) {
            data.rotation = new Vector3f( rotation );
            return this;
        }
        
        RenderDataBuilder setRotation( float rotation ) {
            data.rotation = new Vector3f( 0, 0, rotation );
            return this;
        }
        
        RenderDataBuilder setTexture( Texture texture ) {
            data.texture = texture;
            return this;
        }
        
        RenderDataBuilder setOverlayColour( Vector4fc overlayColour ) {
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