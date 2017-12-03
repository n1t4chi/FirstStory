/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author n1t4chi
 */
public interface OverlayContentManager {
    
    Pane createOverlayPanel();
    
    void init( Stage stage, Scene scene );
    
    void update( double currentTime, int currentFps );
}
