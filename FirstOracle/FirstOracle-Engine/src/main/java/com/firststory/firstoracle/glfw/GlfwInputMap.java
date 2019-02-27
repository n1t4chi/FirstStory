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
        return switch ( action ) {
            case GLFW.GLFW_PRESS -> InputAction.PRESS;
            case GLFW.GLFW_RELEASE -> InputAction.RELEASE;
            case GLFW.GLFW_REPEAT -> InputAction.REPEAT;
            default -> InputAction.UNKNOWN;
        };
    }
    
    public static InputModificators parseInputMods( int mods ) {
        return InputModificators.create( ( mods & GLFW.GLFW_MOD_ALT ) != 0,
            ( mods & GLFW.GLFW_MOD_SHIFT ) != 0,
            ( mods & GLFW.GLFW_MOD_CONTROL ) != 0,
            ( mods & GLFW.GLFW_MOD_SUPER ) != 0 );
    }
}
