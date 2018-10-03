/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.rendering.RenderData;

import java.util.List;

/**
 * Class representing 3D Object, contains texture, UV mapping, vertices and objectTransformations.
 * This abstract class implements some of the boiler plate code like transformations or render data methods
 * @author n1t4chi
 */
public abstract class AbstractPositionableObject3D<
    Transformations extends PositionableObject3DTransformations,
    Vertices extends Vertices3D
> implements 
    PositionableObject3D< Transformations, Vertices >
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