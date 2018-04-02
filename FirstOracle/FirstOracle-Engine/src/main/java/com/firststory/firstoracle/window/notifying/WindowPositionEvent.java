/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.glfw.GlfwWindow;

/**
 * @author n1t4chi
 */
public class WindowPositionEvent {
    
    public final GlfwWindow source;
    public final int xpos;
    public final int ypos;
    
    public WindowPositionEvent( GlfwWindow source, int xpos, int ypos ) {
        
        this.source = source;
        this.xpos = xpos;
        this.ypos = ypos;
    }
}
