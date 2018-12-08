/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.object3D.*;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RegistrableScene extends RenderableScene, RegistrableScene2D, RegistrableScene3D, RegistrableOverlay, RegistrableBackground {
    
    RegistrableScene2D getScene2D();
    
    RegistrableScene3D getScene3D();
    
    RegistrableBackground getBackground();
    
    RegistrableOverlay getOverlay();
    
    @Override
    default void dispose() {
        getScene2D().dispose();
        getScene3D().dispose();
        getBackground().dispose();
        getOverlay().dispose();
    }
    
    @Override
    default void registerBackground( PositionableObject2D< ?, ? > object ) {
        getBackground().registerBackground( object );
    }
    
    @Override
    default void deregisterBackground( PositionableObject2D< ?, ? > object ) {
        getBackground().deregisterBackground( object );
    }
    
    @Override
    default void deregisterAllBackgrounds() {
        getBackground().deregisterAllBackgrounds();
    }
    
    @Override
    default void registerOverlay( PositionableObject2D< ?, ? > object ) {
        getOverlay().registerOverlay( object );
    }
    
    @Override
    default void deregisterOverlay( PositionableObject2D< ?, ? > object ) {
        getOverlay().deregisterOverlay( object );
    }
    
    @Override
    default void deregisterAllOverlays() {
        getOverlay().deregisterAllOverlays();
    }
    
    @Override
    default void registerObject2D( PositionableObject2D< ?, ? > object ) {
        getScene2D().registerObject2D( object );
    }
    
    @Override
    default void registerTerrain2D( Terrain2D< ?, ? > terrain, Index2D index ) {
        getScene2D().registerTerrain2D( terrain, index );
    }
    
    @Override
    default void deregisterObject2D( PositionableObject2D< ?, ? > object ) {
        getScene2D().deregisterObject2D( object );
    }
    
    @Override
    default void deregisterTerrain2D( Terrain2D< ?, ? > terrain, Index2D index ) {
        getScene2D().deregisterTerrain2D( terrain, index );
    }
    
    @Override
    default void deregisterAllObjects2D() {
        getScene2D().deregisterAllObjects2D();
    }
    
    @Override
    default void registerObject3D( PositionableObject3D< ?, ? > object ) {
        getScene3D().registerObject3D( object );
    }
    
    @Override
    default void registerTerrain3D( Terrain3D< ?, ? > terrain, Index3D index ) {
        getScene3D().registerTerrain3D( terrain, index );
    }
    
    @Override
    default void deregisterObject3D( PositionableObject3D< ?, ? > object ) {
        getScene3D().deregisterObject3D( object );
    }
    
    @Override
    default void deregisterTerrain3D( Terrain3D< ?, ? > terrain, Index3D index ) {
        getScene3D().deregisterTerrain3D( terrain, index );
    }
    
    @Override
    default void deregisterAllObjects3D() {
        getScene3D().deregisterAllObjects3D();
    }
    
    @Override
    default Camera2D getBackgroundCamera() {
        return getBackground().getBackgroundCamera();
    }
    
    @Override
    default Colour getBackgroundColour() {
        return getBackground().getBackgroundColour();
    }
    
    @Override
    default Collection< PositionableObject2D< ?, ? > > getBackgroundObjects() {
        return getBackground().getBackgroundObjects();
    }
    
    @Override
    default Camera2D getOverlayCamera() {
        return getOverlay().getOverlayCamera();
    }
    
    @Override
    default Collection< PositionableObject2D< ?, ? > > getOverlayObjects() {
        return getOverlay().getOverlayObjects();
    }
    
    @Override
    default Camera2D getScene2DCamera() {
        return getScene2D().getScene2DCamera();
    }
    
    @Override
    default Collection< PositionableObject2D< ?, ? > > getObjects2D() {
        return getScene2D().getObjects2D();
    }
    
    @Override
    default Terrain2D< ?, ? >[][] getTerrains2D() {
        return getScene2D().getTerrains2D();
    }
    
    @Override
    default Index2D getTerrain2DShift() {
        return getScene2D().getTerrain2DShift();
    }
    
    @Override
    default Camera3D getScene3DCamera() {
        return getScene3D().getScene3DCamera();
    }
    
    @Override
    default Collection< PositionableObject3D< ?, ? > > getObjects3D() {
        return getScene3D().getObjects3D();
    }
    
    @Override
    default Terrain3D< ?, ? >[][][] getTerrains3D() {
        return getScene3D().getTerrains3D();
    }
    
    @Override
    default Index3D getTerrain3DShift() {
        return getScene3D().getTerrain3DShift();
    }
}
