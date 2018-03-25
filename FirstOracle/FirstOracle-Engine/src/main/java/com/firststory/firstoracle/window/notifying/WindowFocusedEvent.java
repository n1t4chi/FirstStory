/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.GLFW.GlfwWindow;

/**
 * @author n1t4chi
 */
public class WindowFocusedEvent {
    
    public final GlfwWindow source;
    public final boolean focused;
    
    public WindowFocusedEvent( GlfwWindow source, boolean focused ) {
        this.source = source;
        this.focused = focused;
    }
}
