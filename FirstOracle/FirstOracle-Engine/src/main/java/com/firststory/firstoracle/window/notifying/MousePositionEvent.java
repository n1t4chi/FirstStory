package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.GLFW.GlfwWindow;

public class MousePositionEvent {
    
    public final GlfwWindow source;
    public final double xpos;
    public final double ypos;
    
    public MousePositionEvent( GlfwWindow source, double xpos, double ypos ) {
        this.source = source;
        this.xpos = xpos;
        this.ypos = ypos;
    }
}
