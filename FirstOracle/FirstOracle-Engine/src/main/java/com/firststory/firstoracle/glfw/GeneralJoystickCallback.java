/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.glfw;

import org.lwjgl.glfw.GLFWJoystickCallbackI;

import java.util.ArrayList;
import java.util.List;

class GeneralJoystickCallback implements GLFWJoystickCallbackI {
    private final List<GlfwWindowContext > windows = new ArrayList<>(  );

    @Override
    public void invoke( int jid, int event ) {
        windows.forEach( window -> window.notifyJoystickListeners( jid, event ) );
    }

    void addWindow(GlfwWindowContext window){
        windows.add( window );
    }

    void removeWindow(GlfwWindowContext window){
        windows.remove( window );
    }
}
