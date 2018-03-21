package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.window.GLFW.GlfwWindow;

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
