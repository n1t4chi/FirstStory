/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.*;

/**
 * Class representing 2D terrain, contains texture, UV mapping, vertices and objectTransformations.
 * This abstract class implements some of the boiler plate code like transformations or render data methods
 * @author n1t4chi
 */
public abstract class AbstractTerrain2D< Vertices extends Vertices2D, PositionCalculatorType extends Position2DCalculator >
    implements
        Terrain2D< Vertices, PositionCalculatorType >,
        MutableTextureObject2D< Identity2DTransformations, Vertices >
{
    private final Map< Position, List< RenderData.RenderDataBuilder > > renderDataBuildersMap = new HashMap<>();
    private final Map< Position, List< RenderData > > renderDatasMap = new HashMap<>();
    private Texture texture = FirstOracleConstants.EMPTY_TEXTURE;
    private Colouring colouring = FirstOracleConstants.EMPTY_COLOURING;
    private Colour overlayColour = FirstOracleConstants.TRANSPARENT;
    
    @Override
    public void storeRenderDataList( Position position, List< RenderData > renderDatas ) {
        renderDatasMap.put( position, renderDatas );
    }
    
    @Override
    public List< RenderData > getStoredRenderDataList( Position position ) {
        return renderDatasMap.get( position );
    }
    
    @Override
    public void storeRenderDataBuilderList( Position position, List< RenderData.RenderDataBuilder > renderDataBuilders ) {
        renderDataBuildersMap.put( position, renderDataBuilders );
    }
    
    @Override
    public List< RenderData.RenderDataBuilder > getStoredRenderDataBuilderList( Position position ) {
        return renderDataBuildersMap.get( position );
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
}
