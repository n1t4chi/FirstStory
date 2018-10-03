/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

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
> implements
    PositionableObject2D< Transformations, Vertices >
{
    Transformations transformations;
    private List< RenderData.RenderDataBuilder > renderDataBuilders;
    private List< RenderData > renderDatas;
    
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
}