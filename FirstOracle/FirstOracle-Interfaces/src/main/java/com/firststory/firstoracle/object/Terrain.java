/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.Index;
import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.data.Rotation;
import com.firststory.firstoracle.data.Scale;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author n1t4chi
 */
public interface Terrain<
    PositionType extends Position,
    ScaleType extends Scale,
    RotationType extends Rotation,
    TransformationsType extends ObjectTransformations< ScaleType, RotationType >,
    BoundingBoxType extends BoundingBox< BoundingBoxType, ?, PositionType >,
    VerticesType extends Vertices< PositionType, BoundingBoxType >,
    IndexType extends Index
> extends
    GraphicObject< PositionType, ScaleType, RotationType, TransformationsType, BoundingBoxType, VerticesType >
{
    /**
     * Store render data list for later use via {@link #getStoredRenderDataList(Position)}
     * @param renderDatas list of render data of this object
     */
    void storeRenderDataList( Position position, List< RenderData > renderDatas );
    
    /**
     * Return previously stored render data list via {@link #storeRenderDataList(Position, List)}  or null if none was stored.
     * @return stored render data list or null if none was stored
     */
    List< RenderData > getStoredRenderDataList( Position position );
    
    /**
     * Store render data builders list for later use via {@link #getStoredRenderDataBuilderList(Position)}
     * @param renderDataBuilders list of render data of this object
     */
    void storeRenderDataBuilderList( Position position, List< RenderData.RenderDataBuilder > renderDataBuilders );
    
    /**
     * Return previously stored render data builders list via {@link #storeRenderDataBuilderList(Position, List)}  or null if none was stored.
     * @return stored render data list or null if none was stored
     */
    List< RenderData.RenderDataBuilder > getStoredRenderDataBuilderList( Position position );
    
    PositionType computePosition(
        IndexType position,
        IndexType arrayShift
    );
    
    /**
     * Returns Render Data for this terrain for given position.
     * For implementations it is advised to reuse all necessary Render Datas,
     * And update them if they would change.
     * Also implementation must provide unique Render Data for each unique position.
     * @param cameraRotation camera rotation
     * @param timeSnapshot time snap shot
     * @return List of Render Datas (terrain, border etc.)
     */
    default List< RenderData > getRenderData( Position position, double timeSnapshot, double cameraRotation ) {
        var renderDataBuilders = getStoredRenderDataBuilderList( position );
        if( renderDataBuilders == null ) {
            renderDataBuilders = createRenderDataBuilders( position );
            storeRenderDataBuilderList( position, renderDataBuilders );
            List< RenderData > renderDatas = new ArrayList<>( renderDataBuilders.size() );
            renderDataBuilders.forEach( renderDataBuilder -> renderDatas.add( renderDataBuilder.build() ) );
            storeRenderDataList( position, renderDatas );
        }
        updateRenderDataBuilders( renderDataBuilders, timeSnapshot, cameraRotation );
        return getStoredRenderDataList( position );
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
    
    private List< RenderData.RenderDataBuilder > createRenderDataBuilders( Position position ) {
        return Arrays.asList(
            RenderData.baseRenderDataForTerrain( this, position ),
            RenderData.borderRenderDataForTerrain( this, position )
        );
    }
    
    
}
