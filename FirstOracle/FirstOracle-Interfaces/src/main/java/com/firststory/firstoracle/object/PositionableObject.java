/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.rendering.RenderData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author n1t4chi
 */
public interface PositionableObject<
    TransformationsType extends PositionableObjectTransformations< ?, ?, ? >,
    BoundingBoxType extends BoundingBox< ?, ?, ? >,
    VerticesType extends Vertices< ?, ? >
> extends
    GraphicObject< TransformationsType, BoundingBoxType, VerticesType >
{
    void setTransformations( TransformationsType transformations );
    
    /**
     * Store render data list for later use via {@link #getStoredRenderDataList()}
     * @param renderDatas list of render data of this object
     */
    void storeRenderDataList( List< RenderData > renderDatas );
    
    /**
     * Return previously stored render data list via {@link #storeRenderDataList(List)} or null if none was stored.
     * @return stored render data list or null if none was stored
     */
    List< RenderData > getStoredRenderDataList();
    
    /**
     * Store render data builders list for later use via {@link #getStoredRenderDataBuilderList()}
     * @param renderDataBuilders list of render data of this object
     */
    void storeRenderDataBuilderList( List< RenderData.RenderDataBuilder > renderDataBuilders );
    
    /**
     * Return previously stored render data builders list via {@link #storeRenderDataBuilderList(List)} or null if none was stored.
     * @return stored render data list or null if none was stored
     */
    List< RenderData.RenderDataBuilder > getStoredRenderDataBuilderList();
    
    /**
     * Returns Render Data for this object.
     * For implementations it is advised to reuse all necessary Render Datas,
     * And update them when they are changed ( new position, texture etc ).
     * @param cameraRotation camera rotation
     * @param timeSnapshot time snap shot
     * @return List of Render Datas (object, border etc.)
     */
    default List< RenderData > getRenderData( double timeSnapshot, double cameraRotation ) {
        var renderDataBuilders = getStoredRenderDataBuilderList();
        if( renderDataBuilders == null ) {
            update();
            renderDataBuilders = getStoredRenderDataBuilderList();
        }
        updateRenderDataBuilders( renderDataBuilders, timeSnapshot, cameraRotation );
        return getStoredRenderDataList();
    }
    
    default void update() {
        List< RenderData.RenderDataBuilder > renderDataBuilders;
        renderDataBuilders = createRenderDataBuilders();
        storeRenderDataBuilderList( renderDataBuilders );
        List< RenderData > renderDatas = new ArrayList<>( renderDataBuilders.size() );
        renderDataBuilders.forEach( renderDataBuilder -> renderDatas.add( renderDataBuilder.build() ) );
        storeRenderDataList( renderDatas );
    }
    
    private void updateRenderDataBuilders( List< RenderData.RenderDataBuilder > renderDataBuilders, double timeSnapshot, double cameraRotation ) {
        var vertexFrame = getCurrentVertexFrame( timeSnapshot );
        var uvMapDirection = getCurrentUvMapDirection( cameraRotation );
        var uvMapFrame = getCurrentUvMapFrame( timeSnapshot );
        renderDataBuilders.forEach( renderDataBuilder -> renderDataBuilder
            .setVertexFrame( vertexFrame )
            .setUvDirection( uvMapDirection )
            .setUvFrame( uvMapFrame )
        );
    }
    
    private List< RenderData.RenderDataBuilder > createRenderDataBuilders() {
        return Arrays.asList(
            RenderData.baseRenderDataForObject( this ),
            RenderData.borderRenderDataForObject( this )
        );
    }
}
