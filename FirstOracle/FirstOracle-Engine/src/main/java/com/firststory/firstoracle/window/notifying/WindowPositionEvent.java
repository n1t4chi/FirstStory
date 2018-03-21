package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.GLFW.GlfwWindow;

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
