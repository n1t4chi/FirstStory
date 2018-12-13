/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.List;

/**
 * @author n1t4chi
 */
public class StaticHexGrid
    extends
        AbstractTerrain2D< Hex2DVertices, HexPositionCalculator >
    implements
        Hex2DGrid,
        StaticObject2D< Identity2DTransformations, Hex2DVertices >,
        MutableTextureObject2D< Identity2DTransformations, Hex2DVertices >
{
    private Texture texture;
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    @Override
    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
    
    @Override
    public UvMap getUvMap() {
        return Hex2DUvMap.getHex2DUvMap();
    }
    
    @Override
    public List< RenderData.RenderDataBuilder > getStoredRenderDataBuilderList( Position position ) {
        var builderList = super.getStoredRenderDataBuilderList( position );
        if( builderList != null ) {
            builderList.forEach( builder -> builder.setUvMap( getUvMap() ) );
        }
        return builderList;
    }
}
