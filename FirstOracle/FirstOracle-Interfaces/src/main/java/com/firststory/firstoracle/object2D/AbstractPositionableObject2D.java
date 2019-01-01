/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.List;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public abstract class AbstractPositionableObject2D<
    Transformations extends PositionableObject2DTransformations,
    Vertices extends Vertices2D
>
    implements
        PositionableObject2D< Transformations, Vertices >,
        MutableTextureObject2D< Transformations, Vertices >
{
    private Transformations transformations;
    private List< RenderData.RenderDataBuilder > renderDataBuilders;
    private List< RenderData > renderDatas;
    private Texture texture = FirstOracleConstants.EMPTY_TEXTURE;
    private Colouring colouring = FirstOracleConstants.EMPTY_COLOURING;
    private Colour overlayColour = FirstOracleConstants.TRANSPARENT;
    private float maxAlphaChannel = 1f;
    
    @Override
    public Transformations getTransformations() {
        return transformations;
    }
    
    @Override
    public void setTransformations( Transformations transformations ) {
        this.transformations = transformations;
    }
    
    @Override
    public void storeRenderDataList( List< RenderData > renderDatas ) {
        this.renderDatas = renderDatas;
    }
    
    @Override
    public List< RenderData > getStoredRenderDataList() {
        return renderDatas;
    }
    
    @Override
    public void storeRenderDataBuilderList( List< RenderData.RenderDataBuilder > renderDataBuilders ) {
        this.renderDataBuilders = renderDataBuilders;
    }
    
    @Override
    public List< RenderData.RenderDataBuilder > getStoredRenderDataBuilderList() {
        return renderDataBuilders;
    }
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    @Override
    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
    
    @Override
    public Colouring getColouring() {
        return colouring;
    }
    
    public void setColouring( Colouring colouring ) {
        this.colouring = colouring;
    }
    
    @Override
    public Colour getOverlayColour() {
        return overlayColour;
    }
    
    public void setOverlayColour( Colour overlayColour ) {
        this.overlayColour = overlayColour;
    }
    
    @Override
    public float getMaxAlphaChannel() {
        return maxAlphaChannel;
    }
    
    public void setMaxAlphaChannel( float maxAlphaChannel ) {
        this.maxAlphaChannel = maxAlphaChannel;
    }
}