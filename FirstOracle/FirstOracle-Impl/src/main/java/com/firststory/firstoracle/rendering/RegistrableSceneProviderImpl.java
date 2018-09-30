/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.scene.RenderableScene;

import java.util.HashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class RegistrableSceneProviderImpl< SceneType extends RenderableScene > implements RegistrableSceneProvider< SceneType > {
    
    private final Map< Integer, SceneType > scenes = new HashMap<>(  );
    private Integer currentIndex = null;
    
    @Override
    public void registerScene( int index, SceneType scene ) {
        scenes.put( index, scene );
        if( currentIndex == null ) {
            currentIndex = index;
        }
    }
    
    public Integer getCurrentIndex() {
        return currentIndex;
    }
    
    public SceneType getScene( int index ) {
        return scenes.get( index );
    }
    
    @Override
    public void setActiveScene( int index ) {
        currentIndex = index;
    }
    
    @Override
    public SceneType getNextScene() {
        return getScene( currentIndex );
    }
    
    @Override
    public void removeScene( int index ) {
        RenderableScene removedScene = scenes.remove( index );
        if( currentIndex == index ) {
            currentIndex = null;
        }
    }
    
    @Override
    public void removeAll() {
        scenes.forEach( ( index, scene ) -> scene.dispose() );
        scenes.clear();
    }
    
    public void dispose() {
        removeAll();
    }
}
