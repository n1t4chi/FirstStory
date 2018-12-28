/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class PositionableObject2DImpl
    extends
        AbstractPositionableObject2D< MutablePositionable2DTransformations, Vertices2D >
    implements
        MutableTransformationsObject2D< Vertices2D >
{
    
    private UvMap uvMap = PlaneUvMap.getPlaneUvMap();
    private Texture texture = FirstOracleConstants.EMPTY_TEXTURE;
    private Vertices2D vertices = AbsolutePlane2DVertices.getPlane2DVertices();
    private Colouring colouring = FirstOracleConstants.EMPTY_COLOURING;
    private Colour colour = FirstOracleConstants.WHITE;
    private DirectionController directionController = direction -> 0;
    private FrameController frameController = time -> 0;
    
    public void setDirectionController( DirectionController directionController ) {
        this.directionController = directionController;
    }
    
    public void setFrameController( FrameController frameController ) {
        this.frameController = frameController;
    }
    
    public PositionableObject2DImpl() {
        setTransformations( new MutablePositionable2DTransformations() );
    }
    
    public void setColouring( Colouring colouring ) {
        this.colouring = colouring;
    }
    
    public void setUvMap( UvMap uvMap ) {
        this.uvMap = uvMap;
    }
    
    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
    
    public void setVertices( Vertices2D vertices ) {
        this.vertices = vertices;
    }
    
    @Override
    public Colouring getColouring() {
        return colouring;
    }
    
    @Override
    public Colour getOverlayColour() {
        return colour;
    }
    
    public void setOverlayColour( Colour colour ) {
        this.colour = colour;
    }
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    @Override
    public UvMap getUvMap() {
        return uvMap;
    }
    
    @Override
    public Vertices2D getVertices() {
        return vertices;
    }
    
    @Override
    public int getCurrentUvMapDirection( double currentCameraRotation ) {
        return directionController.getCurrentDirection( currentCameraRotation );
    }
    
    @Override
    public int getCurrentUvMapFrame( double currentTimeSnapshot ) {
        return frameController.getCurrentFrame( currentTimeSnapshot );
    }
    
    @Override
    public int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
}
