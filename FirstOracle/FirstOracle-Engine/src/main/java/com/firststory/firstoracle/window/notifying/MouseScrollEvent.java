package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.GLFW.GlfwWindow;

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
