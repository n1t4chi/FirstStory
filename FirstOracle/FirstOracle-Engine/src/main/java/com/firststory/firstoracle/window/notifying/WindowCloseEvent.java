/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.GLFW.GlfwWindow;

/**
 * @author n1t4chi
 */
public class WindowCloseEvent {
    
    public final GlfwWindow source;
    
    public WindowCloseEvent( GlfwWindow source ) {
        this.source = source;
    }
}
