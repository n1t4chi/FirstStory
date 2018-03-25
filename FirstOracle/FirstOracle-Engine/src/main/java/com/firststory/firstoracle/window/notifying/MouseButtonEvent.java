/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.GLFW.GlfwWindow;

/**
 * @author n1t4chi
 */
public class MouseButtonEvent {
    
    public final GlfwWindow source;
    public final int button;
    public final int action;
    public final int mods;
    
    public MouseButtonEvent( GlfwWindow source, int button, int action, int mods ) {
        this.source = source;
        this.button = button;
        this.action = action;
        this.mods = mods;
    }
}
