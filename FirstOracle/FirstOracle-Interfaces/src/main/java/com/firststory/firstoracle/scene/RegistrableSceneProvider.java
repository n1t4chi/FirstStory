/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

/**
 * @author n1t4chi
 */
public interface RegistrableSceneProvider< SceneType extends RenderableScene > extends SceneProvider {
    
    void registerScene( int index, SceneType scene );
    
    void setActiveScene( int index );
    
    void removeScene( int index );
    
    void removeAll();
    
    @Override
    SceneType getNextScene();
    
    Integer getCurrentIndex();
    
    SceneType getScene( int index );
}
