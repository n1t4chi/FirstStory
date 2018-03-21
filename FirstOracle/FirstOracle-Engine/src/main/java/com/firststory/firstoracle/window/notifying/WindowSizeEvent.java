package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.GLFW.GlfwWindow;

public class WindowSizeEvent {
    
    private final GlfwWindow source;
    private final int width;
    private final int height;
    
    public GlfwWindow getSource() {
        return source;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public WindowSizeEvent( GlfwWindow source, int width, int height ) {
        this.source = source;
        this.width = width;
        this.height = height;
    }
}
