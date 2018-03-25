/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.GLFW.GlfwWindow;

/**
 * @author n1t4chi
 */
public class JoystickEvent {
    
    public final GlfwWindow source;
    public final int jid;
    public final int event;
    
    public JoystickEvent( GlfwWindow source, int jid, int event ) {
        this.source = source;
        this.jid = jid;
        this.event = event;
    }
}
