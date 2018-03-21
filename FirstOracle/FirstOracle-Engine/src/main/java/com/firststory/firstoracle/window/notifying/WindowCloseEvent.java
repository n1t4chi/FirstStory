package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.GLFW.GlfwWindow;

public class WindowCloseEvent {
    
    public final GlfwWindow source;
    
    public WindowCloseEvent( GlfwWindow source ) {
        this.source = source;
    }
}
