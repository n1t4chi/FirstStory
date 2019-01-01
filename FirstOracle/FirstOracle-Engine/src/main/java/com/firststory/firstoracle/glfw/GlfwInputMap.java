/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.glfw;

import com.firststory.firstoracle.input.*;
import org.lwjgl.glfw.GLFW;

/**
 * @author n1t4chi
 */
public class GlfwInputMap {
    public static InputAction parseInputAction( int action ) {
        switch ( action ) {
            case GLFW.GLFW_PRESS: return InputAction.PRESS;
            case GLFW.GLFW_RELEASE: return InputAction.RELEASE;
            case GLFW.GLFW_REPEAT: return InputAction.REPEAT;
            default: return InputAction.UNKNOWN;
        }
    }
    
    public static InputModificators parseInputMods( int mods ) {
        return InputModificators.create( ( mods & GLFW.GLFW_MOD_ALT ) != 0,
            ( mods & GLFW.GLFW_MOD_SHIFT ) != 0,
            ( mods & GLFW.GLFW_MOD_CONTROL ) != 0,
            ( mods & GLFW.GLFW_MOD_SUPER ) != 0 );
    }
}
