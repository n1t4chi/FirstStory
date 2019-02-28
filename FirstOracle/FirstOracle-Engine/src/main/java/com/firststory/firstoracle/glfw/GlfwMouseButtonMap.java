/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.glfw;

import com.firststory.firstoracle.input.*;
import com.firststory.firsttools.FirstToolsConstants;
import org.lwjgl.glfw.GLFW;

import java.util.logging.*;

import static com.firststory.firstoracle.glfw.GlfwInputMap.*;

/**
 * @author n1t4chi
 */
public class GlfwMouseButtonMap {
    
    private static final Logger logger = FirstToolsConstants.getLogger( GlfwMouseButtonMap.class );
    
    public static MouseButton parseMouseButtonCode( int button, int action, int mods ) {
        logger.log( Level.FINEST, () -> mouseButtonStrokeToString( button, action, mods ) );
        return MouseButton.create( parseMouseButtonCode( button ), parseInputAction( action ), parseInputMods( mods ) );
    }
    
    public static String mouseButtonStrokeToString( int mouseButtonCode, int action, int mods ) {
        return "" +
            "input: " + mouseButtonCode + " -> " + parseMouseButtonCode( mouseButtonCode ) + "\n" +
            "action: " + action + " -> " + parseInputAction( action ) + "\n" +
            "mods: " + mods + " -> " + parseInputMods( mods ) + "\n"
        ;
    }
    
    public static int parseMouseButtonScanCode( int scancode ) {
        return scancode;
    }
    
    public static MouseButtonCode parseMouseButtonCode( int mouseButton ) {
        return switch ( mouseButton ) {
            case GLFW.GLFW_MOUSE_BUTTON_1 -> MouseButtonCode.BUTTON_1;
            case GLFW.GLFW_MOUSE_BUTTON_2 -> MouseButtonCode.BUTTON_2;
            case GLFW.GLFW_MOUSE_BUTTON_3 -> MouseButtonCode.BUTTON_3;
            case GLFW.GLFW_MOUSE_BUTTON_4 -> MouseButtonCode.BUTTON_4;
            case GLFW.GLFW_MOUSE_BUTTON_5 -> MouseButtonCode.BUTTON_5;
            case GLFW.GLFW_MOUSE_BUTTON_6 -> MouseButtonCode.BUTTON_6;
            case GLFW.GLFW_MOUSE_BUTTON_7 -> MouseButtonCode.BUTTON_7;
            case GLFW.GLFW_MOUSE_BUTTON_8 -> MouseButtonCode.BUTTON_8;
            default -> MouseButtonCode.UNKNOWN;
        };
    }
}
