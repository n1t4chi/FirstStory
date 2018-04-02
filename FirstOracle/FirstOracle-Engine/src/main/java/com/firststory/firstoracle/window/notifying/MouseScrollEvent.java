/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.glfw.GlfwWindow;

/**
 * @author n1t4chi
 */
public class MouseScrollEvent {
    
    public final GlfwWindow source;
    public final double xoffset;
    public final double yoffset;
    
    public MouseScrollEvent( GlfwWindow source, double xoffset, double yoffset ) {
        this.source = source;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
    }
}
