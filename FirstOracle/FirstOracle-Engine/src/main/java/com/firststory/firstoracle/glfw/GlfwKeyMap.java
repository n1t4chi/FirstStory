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
public class GlfwKeyMap {
    private static final Logger logger = FirstToolsConstants.getLogger( GlfwKeyMap.class );
    
    public static Key parseKeyCode( int keyCode, int scancode, int action, int mods ) {
        logger.log( Level.FINEST, () -> keystrokeToString( keyCode, action, mods ) );
        return Key.create( parseKeyCode( keyCode ), parseInputAction( action ), parseInputMods( mods ) );
    }
    
    public static String keystrokeToString(
        int keyCode,
        int action,
        int mods
    ) {
        return "" +
            "input: "+keyCode+" -> " + parseKeyCode( keyCode ) + "\n" +
            "action: "+action+" -> " + parseInputAction( action ) + "\n" +
            "mods: "+mods+" -> "+parseInputMods( mods ) +"\n"
        ;
    }
    
    public static int parseKeyScanCode( int scancode ) {
        return scancode;
    }
    
    public static KeyCode parseKeyCode(int key){
        return switch ( key ) {
            case GLFW.GLFW_KEY_SPACE -> KeyCode.KEY_SPACE;
            case GLFW.GLFW_KEY_APOSTROPHE -> KeyCode.KEY_APOSTROPHE;
            case GLFW.GLFW_KEY_COMMA -> KeyCode.KEY_COMMA;
            case GLFW.GLFW_KEY_MINUS -> KeyCode.KEY_MINUS;
            case GLFW.GLFW_KEY_PERIOD -> KeyCode.KEY_PERIOD;
            case GLFW.GLFW_KEY_SLASH -> KeyCode.KEY_SLASH;
            case GLFW.GLFW_KEY_0 -> KeyCode.KEY_0;
            case GLFW.GLFW_KEY_1 -> KeyCode.KEY_1;
            case GLFW.GLFW_KEY_2 -> KeyCode.KEY_2;
            case GLFW.GLFW_KEY_3 -> KeyCode.KEY_3;
            case GLFW.GLFW_KEY_4 -> KeyCode.KEY_4;
            case GLFW.GLFW_KEY_5 -> KeyCode.KEY_5;
            case GLFW.GLFW_KEY_6 -> KeyCode.KEY_6;
            case GLFW.GLFW_KEY_7 -> KeyCode.KEY_7;
            case GLFW.GLFW_KEY_8 -> KeyCode.KEY_8;
            case GLFW.GLFW_KEY_9 -> KeyCode.KEY_9;
            case GLFW.GLFW_KEY_SEMICOLON -> KeyCode.KEY_SEMICOLON;
            case GLFW.GLFW_KEY_EQUAL -> KeyCode.KEY_EQUAL;
            case GLFW.GLFW_KEY_A -> KeyCode.KEY_A;
            case GLFW.GLFW_KEY_B -> KeyCode.KEY_B;
            case GLFW.GLFW_KEY_C -> KeyCode.KEY_C;
            case GLFW.GLFW_KEY_D -> KeyCode.KEY_D;
            case GLFW.GLFW_KEY_E -> KeyCode.KEY_E;
            case GLFW.GLFW_KEY_F -> KeyCode.KEY_F;
            case GLFW.GLFW_KEY_G -> KeyCode.KEY_G;
            case GLFW.GLFW_KEY_H -> KeyCode.KEY_H;
            case GLFW.GLFW_KEY_I -> KeyCode.KEY_I;
            case GLFW.GLFW_KEY_J -> KeyCode.KEY_J;
            case GLFW.GLFW_KEY_K -> KeyCode.KEY_K;
            case GLFW.GLFW_KEY_L -> KeyCode.KEY_L;
            case GLFW.GLFW_KEY_M -> KeyCode.KEY_M;
            case GLFW.GLFW_KEY_N -> KeyCode.KEY_N;
            case GLFW.GLFW_KEY_O -> KeyCode.KEY_O;
            case GLFW.GLFW_KEY_P -> KeyCode.KEY_P;
            case GLFW.GLFW_KEY_Q -> KeyCode.KEY_Q;
            case GLFW.GLFW_KEY_R -> KeyCode.KEY_R;
            case GLFW.GLFW_KEY_S -> KeyCode.KEY_S;
            case GLFW.GLFW_KEY_T -> KeyCode.KEY_T;
            case GLFW.GLFW_KEY_U -> KeyCode.KEY_U;
            case GLFW.GLFW_KEY_V -> KeyCode.KEY_V;
            case GLFW.GLFW_KEY_W -> KeyCode.KEY_W;
            case GLFW.GLFW_KEY_X -> KeyCode.KEY_X;
            case GLFW.GLFW_KEY_Y -> KeyCode.KEY_Y;
            case GLFW.GLFW_KEY_Z -> KeyCode.KEY_Z;
            case GLFW.GLFW_KEY_LEFT_BRACKET -> KeyCode.KEY_LEFT_BRACKET;
            case GLFW.GLFW_KEY_BACKSLASH -> KeyCode.KEY_BACKSLASH;
            case GLFW.GLFW_KEY_RIGHT_BRACKET -> KeyCode.KEY_RIGHT_BRACKET;
            case GLFW.GLFW_KEY_GRAVE_ACCENT -> KeyCode.KEY_GRAVE_ACCENT;
            case GLFW.GLFW_KEY_WORLD_1 -> KeyCode.KEY_WORLD_1;
            case GLFW.GLFW_KEY_WORLD_2 -> KeyCode.KEY_WORLD_2;
            case GLFW.GLFW_KEY_ESCAPE -> KeyCode.KEY_ESCAPE;
            case GLFW.GLFW_KEY_ENTER -> KeyCode.KEY_ENTER;
            case GLFW.GLFW_KEY_TAB -> KeyCode.KEY_TAB;
            case GLFW.GLFW_KEY_BACKSPACE -> KeyCode.KEY_BACKSPACE;
            case GLFW.GLFW_KEY_INSERT -> KeyCode.KEY_INSERT;
            case GLFW.GLFW_KEY_DELETE -> KeyCode.KEY_DELETE;
            case GLFW.GLFW_KEY_RIGHT -> KeyCode.KEY_RIGHT;
            case GLFW.GLFW_KEY_LEFT -> KeyCode.KEY_LEFT;
            case GLFW.GLFW_KEY_DOWN -> KeyCode.KEY_DOWN;
            case GLFW.GLFW_KEY_UP -> KeyCode.KEY_UP;
            case GLFW.GLFW_KEY_PAGE_UP -> KeyCode.KEY_PAGE_UP;
            case GLFW.GLFW_KEY_PAGE_DOWN -> KeyCode.KEY_PAGE_DOWN;
            case GLFW.GLFW_KEY_HOME -> KeyCode.KEY_HOME;
            case GLFW.GLFW_KEY_END -> KeyCode.KEY_END;
            case GLFW.GLFW_KEY_CAPS_LOCK -> KeyCode.KEY_CAPS_LOCK;
            case GLFW.GLFW_KEY_SCROLL_LOCK -> KeyCode.KEY_SCROLL_LOCK;
            case GLFW.GLFW_KEY_NUM_LOCK -> KeyCode.KEY_NUM_LOCK;
            case GLFW.GLFW_KEY_PRINT_SCREEN -> KeyCode.KEY_PRINT_SCREEN;
            case GLFW.GLFW_KEY_PAUSE -> KeyCode.KEY_PAUSE;
            case GLFW.GLFW_KEY_F1 -> KeyCode.KEY_F1;
            case GLFW.GLFW_KEY_F2 -> KeyCode.KEY_F2;
            case GLFW.GLFW_KEY_F3 -> KeyCode.KEY_F3;
            case GLFW.GLFW_KEY_F4 -> KeyCode.KEY_F4;
            case GLFW.GLFW_KEY_F5 -> KeyCode.KEY_F5;
            case GLFW.GLFW_KEY_F6 -> KeyCode.KEY_F6;
            case GLFW.GLFW_KEY_F7 -> KeyCode.KEY_F7;
            case GLFW.GLFW_KEY_F8 -> KeyCode.KEY_F8;
            case GLFW.GLFW_KEY_F9 -> KeyCode.KEY_F9;
            case GLFW.GLFW_KEY_F10 -> KeyCode.KEY_F10;
            case GLFW.GLFW_KEY_F11 -> KeyCode.KEY_F11;
            case GLFW.GLFW_KEY_F12 -> KeyCode.KEY_F12;
            case GLFW.GLFW_KEY_F13 -> KeyCode.KEY_F13;
            case GLFW.GLFW_KEY_F14 -> KeyCode.KEY_F14;
            case GLFW.GLFW_KEY_F15 -> KeyCode.KEY_F15;
            case GLFW.GLFW_KEY_F16 -> KeyCode.KEY_F16;
            case GLFW.GLFW_KEY_F17 -> KeyCode.KEY_F17;
            case GLFW.GLFW_KEY_F18 -> KeyCode.KEY_F18;
            case GLFW.GLFW_KEY_F19 -> KeyCode.KEY_F19;
            case GLFW.GLFW_KEY_F20 -> KeyCode.KEY_F20;
            case GLFW.GLFW_KEY_F21 -> KeyCode.KEY_F21;
            case GLFW.GLFW_KEY_F22 -> KeyCode.KEY_F22;
            case GLFW.GLFW_KEY_F23 -> KeyCode.KEY_F23;
            case GLFW.GLFW_KEY_F24 -> KeyCode.KEY_F24;
            case GLFW.GLFW_KEY_F25 -> KeyCode.KEY_F25;
            case GLFW.GLFW_KEY_KP_0 -> KeyCode.KEY_KP_0;
            case GLFW.GLFW_KEY_KP_1 -> KeyCode.KEY_KP_1;
            case GLFW.GLFW_KEY_KP_2 -> KeyCode.KEY_KP_2;
            case GLFW.GLFW_KEY_KP_3 -> KeyCode.KEY_KP_3;
            case GLFW.GLFW_KEY_KP_4 -> KeyCode.KEY_KP_4;
            case GLFW.GLFW_KEY_KP_5 -> KeyCode.KEY_KP_5;
            case GLFW.GLFW_KEY_KP_6 -> KeyCode.KEY_KP_6;
            case GLFW.GLFW_KEY_KP_7 -> KeyCode.KEY_KP_7;
            case GLFW.GLFW_KEY_KP_8 -> KeyCode.KEY_KP_8;
            case GLFW.GLFW_KEY_KP_9 -> KeyCode.KEY_KP_9;
            case GLFW.GLFW_KEY_KP_DECIMAL -> KeyCode.KEY_KP_DECIMAL;
            case GLFW.GLFW_KEY_KP_DIVIDE -> KeyCode.KEY_KP_DIVIDE;
            case GLFW.GLFW_KEY_KP_MULTIPLY -> KeyCode.KEY_KP_MULTIPLY;
            case GLFW.GLFW_KEY_KP_SUBTRACT -> KeyCode.KEY_KP_SUBTRACT;
            case GLFW.GLFW_KEY_KP_ADD -> KeyCode.KEY_KP_ADD;
            case GLFW.GLFW_KEY_KP_ENTER -> KeyCode.KEY_KP_ENTER;
            case GLFW.GLFW_KEY_KP_EQUAL -> KeyCode.KEY_KP_EQUAL;
            case GLFW.GLFW_KEY_LEFT_SHIFT -> KeyCode.KEY_LEFT_SHIFT;
            case GLFW.GLFW_KEY_LEFT_CONTROL -> KeyCode.KEY_LEFT_CONTROL;
            case GLFW.GLFW_KEY_LEFT_ALT -> KeyCode.KEY_LEFT_ALT;
            case GLFW.GLFW_KEY_LEFT_SUPER -> KeyCode.KEY_LEFT_SUPER;
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> KeyCode.KEY_RIGHT_SHIFT;
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> KeyCode.KEY_RIGHT_CONTROL;
            case GLFW.GLFW_KEY_RIGHT_ALT -> KeyCode.KEY_RIGHT_ALT;
            case GLFW.GLFW_KEY_RIGHT_SUPER -> KeyCode.KEY_RIGHT_SUPER;
            case GLFW.GLFW_KEY_MENU -> KeyCode.KEY_MENU;
            default -> KeyCode.UNKNOWN;
        };
    }
}
