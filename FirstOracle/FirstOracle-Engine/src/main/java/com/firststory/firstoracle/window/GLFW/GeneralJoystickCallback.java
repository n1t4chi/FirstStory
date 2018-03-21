package com.firststory.firstoracle.window.GLFW;

import org.lwjgl.glfw.GLFWJoystickCallbackI;

import java.util.ArrayList;
import java.util.List;

class GeneralJoystickCallback implements GLFWJoystickCallbackI {
    private final List<GlfwWindow> windows = new ArrayList<>(  );

    @Override
    public void invoke( int jid, int event ) {
        windows.forEach( window -> window.notifyJoystickListeners( jid, event ) );
    }

    void addWindow(GlfwWindow window){
        windows.add( window );
    }

    void removeWindow(GlfwWindow window){
        windows.remove( window );
    }
}
