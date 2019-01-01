/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.gui.GuiApplicationData;
import com.firststory.firstoracle.gui.GuiFrameworkProvider;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.rendering.Renderer;
import com.firststory.firstoracle.rendering.RenderingFrameworkProvider;
import com.firststory.firstoracle.scene.RegistrableScene;
import com.firststory.firstoracle.scene.RegistrableSceneImpl;
import com.firststory.firstoracle.scene.RegistrableSceneProvider;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public class RegistrableWindow extends WindowImpl {
    
    private final RegistrableSceneProvider< RegistrableScene > provider;
    
    public RegistrableWindow(
        WindowSettings settings,
        GuiApplicationData< ? > application,
        Renderer renderer,
        WindowFrameworkProvider windowFrameworkProvider,
        RenderingFrameworkProvider renderingFrameworkProvider,
        GuiFrameworkProvider< ? > guiFrameworkProvider,
        RenderLoop renderLoop,
        RegistrableSceneProvider< RegistrableScene > provider
    ) {
        super(
            settings,
            application,
            renderer,
            windowFrameworkProvider,
            renderingFrameworkProvider,
            guiFrameworkProvider,
            renderLoop
        );
        this.provider = provider;
    }
    
    public void registerScene( int index, RegistrableScene scene ) {
        provider.registerScene( index, scene );
    }
    
    public void createNewScene2D( int index, Index2D terrain2DSize, Index2D terrain2DShift ) {
        provider.registerScene( index, RegistrableSceneImpl.provide2D( terrain2DSize, terrain2DShift ) );
    }
    
    public void createNewScene3D( int index, Index3D terrain3DSize, Index3D terrain3DShift ) {
        provider.registerScene( index, RegistrableSceneImpl.provide3D( terrain3DSize, terrain3DShift ) );
    }
    
    public void createNewScene( int index, Index2D terrain2DSize, Index2D terrain2DShift, Index3D terrain3DSize, Index3D terrain3DShift ) {
        provider.registerScene( index, RegistrableSceneImpl.provide( terrain2DSize, terrain2DShift, terrain3DSize, terrain3DShift ) );
    }
    
    public void createNewOptimisedScene( int index, Index2D terrain2DSize, Index2D terrain2DShift, Index3D terrain3DSize, Index3D terrain3DShift ) {
        provider.registerScene( index, RegistrableSceneImpl.provideOptimised( terrain2DSize, terrain2DShift, terrain3DSize, terrain3DShift ) );
    }
    
    public Integer getCurrentSceneIndex() {
        return provider.getCurrentIndex();
    }
    
    public RegistrableScene getScene( int index ) {
        return provider.getScene( index );
    }
    
    public void setActiveScene( int index ) {
        provider.setActiveScene( index );
    }
    
    public void removeScene( int index ) {
        provider.removeScene( index );
    }
    
    public void registerOverlay( int index, PositionableObject2D< ?, ? > object ) {
        getScene( index ).registerOverlay( object );
    }
    
    public void deregisterOverlay( int index, PositionableObject2D< ?, ? > object ) {
        getScene( index ).deregisterOverlay( object );
    }
    
    public void deregisterAllOverlays( int index ) {
        getScene( index ).deregisterAllOverlays();
    }
    
    public void registerMultipleOverlays( int index, Collection< PositionableObject2D< ?, ? > > objects ) {
        getScene( index ).registerMultipleOverlays( objects );
    }
    
    public void deregisterMultipleOverlays( int index, Collection< PositionableObject2D< ?, ? > > objects ) {
        getScene( index ).deregisterMultipleOverlays( objects );
    }
    
    public void registerBackground( int index, PositionableObject2D< ?, ? > object ) {
        getScene( index ).registerBackground( object );
    }
    
    public void deregisterBackground( int index, PositionableObject2D< ?, ? > object ) {
        getScene( index ).deregisterBackground( object );
    }
    
    public void deregisterAllBackgrounds( int index ) {
        getScene( index ).deregisterAllBackgrounds();
    }
    
    public void deregisterMultipleBackgrounds( int index, Collection< PositionableObject2D< ?, ? > > objects ) {
        getScene( index ).deregisterMultipleBackgrounds( objects );
    }
    
    public void registerMultipleBackgrounds( int index, Collection< PositionableObject2D< ?, ? > > objects ) {
        getScene( index ).registerMultipleBackgrounds( objects );
    }
    
    public void registerObject3D( int index, PositionableObject3D< ?, ? > object ) {
        getScene( index ).registerObject3D( object );
    }
    
    public void deregisterObject3D( int index, PositionableObject3D< ?, ? > object ) {
        getScene( index ).deregisterObject3D( object );
    }
    
    public void registerTerrains3D( int index, Terrain3D< ? > terrain, Index3D terrainIndex ) {
        getScene( index ).registerTerrain3D( terrain, terrainIndex);
    }
    
    public void deregisterTerrains3D( int index, Terrain3D< ? > terrain, Index3D terrainIndex ) {
        getScene( index ).deregisterTerrain3D( terrain, terrainIndex );
    }
    
    public void deregisterAllObjects3D( int index ) {
        getScene( index ).deregisterAllObjects3D();
    }
    
    public void registerTerrains3D( int index, Terrain3D< ? > terrain, int x, int y, int z ) {
        getScene( index ).registerTerrain3D( terrain, x, y, z );
    }
    
    public void deregisterTerrains3D( int index, Terrain3D< ? > terrain, int x, int y, int z ) {
        getScene( index ).deregisterTerrain3D( terrain, x, y, z );
    }
    
    public void deregisterMultipleObjects3D( int index, Collection< PositionableObject3D< ?, ? > > objects ) {
        getScene( index ).deregisterMultipleObjects3D( objects );
    }
    
    public void deregisterMultipleTerrains3D( int index, Terrain3D< ? > terrain, Collection< Index3D > positions ) {
        getScene( index ).deregisterMultipleTerrains3D( terrain, positions );
    }
    
    public void registerMultipleObjects3D( int index, Collection< PositionableObject3D< ?, ? > > objects ) {
        getScene( index ).registerMultipleObjects3D( objects );
    }
    
    public void registerMultipleTerrains3D( int index, Terrain3D< ? > terrain, Collection< Index3D > indices ) {
        getScene( index ).registerMultipleTerrains3D( terrain, indices );
    }
    
    public void registerMultipleTerrains3D( int index, Terrain3D< ? >[][][] terrainsXYZ ) {
        getScene( index ).registerMultipleTerrains3D( terrainsXYZ );
    }
    
    public void registerObject2D( int index, PositionableObject2D< ?, ? > object ) {
        getScene( index ).registerObject2D( object );
    }
    
    public void deregisterObject2D( int index, PositionableObject2D< ?, ? > object ) {
        getScene( index ).deregisterObject2D( object );
    }
    
    public void registerTerrains2D( int index, Terrain2D< ? > terrain, Index2D terrainIndex ) {
        getScene( index ).registerTerrain2D( terrain, terrainIndex );
    }
    
    public void deregisterTerrains2D( int index, Terrain2D< ? > terrain, Index2D terrainIndex ) {
        getScene( index ).deregisterTerrain2D( terrain, terrainIndex );
    }
    
    public void deregisterAllObjects2D( int index ) {
        getScene( index ).deregisterAllObjects2D();
    }
    
    public void deregisterTerrains2D( int index, Terrain2D< ? > terrain, int x, int y ) {
        getScene( index ).deregisterTerrain2D( terrain, x, y );
    }
    
    public void registerTerrains2D( int index, Terrain2D< ? > terrain, int x, int y ) {
        getScene( index ).registerTerrain2D( terrain, x, y );
    }
    
    public void deregisterMultipleObjects2D( int index, Collection< PositionableObject2D< ?, ? > > objects ) {
        getScene( index ).deregisterMultipleObjects2D( objects );
    }
    
    public void deregisterMultipleTerrains2D( int index, Terrain2D< ? > terrain, Collection< Index2D > indices ) {
        getScene( index ).deregisterMultipleTerrains2D( terrain, indices );
    }
    
    public void registerMultipleObjects2D( int index, Collection< PositionableObject2D< ?, ? > > objects ) {
        getScene( index ).registerMultipleObjects2D( objects );
    }
    
    public void registerMultipleTerrains2D( int index, Terrain2D< ? > terrain, Collection< Index2D > indices ) {
        getScene( index ).registerMultipleTerrains2D( terrain, indices );
    }
    
    public void registerMultipleTerrains2D( int index, Terrain2D< ? >[][] terrainsXY ) {
        getScene( index ).registerMultipleTerrains2D( terrainsXY );
    }
    
    public void setBackgroundCamera( int index, Camera2D camera ) {
        getScene( index ).setBackgroundCamera( camera );
    }
    
    public void setBackgroundColour( int index, Colour colour ) {
        getScene( index ).setBackgroundColour( colour );
    }
    
    public void setOverlayCamera( int index, Camera2D camera ) {
        getScene( index ).setOverlayCamera( camera );
    }
    
    public void setScene2DCamera( int index, Camera2D camera ) {
        getScene( index ).setScene2DCamera( camera );
    }
    
    public void setScene3DCamera( int index, Camera3D camera ) {
        getScene( index ).setScene3DCamera( camera );
    }
    
}
