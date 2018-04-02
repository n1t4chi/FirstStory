/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.glfw.GlfwWindow;

/**
 * @author n1t4chi
 */
public class WindowSizeEvent {
    
    private final GlfwWindow source;
    private final int width;
    private final int height;
    
    public GlfwWindow getSource() {
        return source;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public WindowSizeEvent( GlfwWindow source, int width, int height ) {
        this.source = source;
        this.width = width;
        this.height = height;
    }
}
