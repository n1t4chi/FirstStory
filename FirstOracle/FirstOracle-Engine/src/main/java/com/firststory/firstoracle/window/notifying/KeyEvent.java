package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.GLFW.GlfwWindow;

public class KeyEvent {
    
    public final GlfwWindow source;
    public final int key;
    public final int scancode;
    public final int action;
    public final int mods;
    public KeyEvent( GlfwWindow source, int key, int scancode, int action, int mods ) {
        this.source = source;
        this.key = key;
        this.scancode = scancode;
        this.action = action;
        this.mods = mods;
    }
}
