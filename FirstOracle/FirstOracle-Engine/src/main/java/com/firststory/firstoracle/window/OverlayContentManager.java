/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import javafx.scene.layout.Pane;

/**
 * @author n1t4chi
 */
public interface OverlayContentManager {

    Pane createOverlayPanel();

    void init();

    void update( double currentTime, int currentFps );
}
