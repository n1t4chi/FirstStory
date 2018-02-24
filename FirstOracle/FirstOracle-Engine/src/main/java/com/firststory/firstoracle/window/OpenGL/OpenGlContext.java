/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.OpenGL;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.KHRDebug.GL_DEBUG_OUTPUT;

public class OpenGlContext {
    private static OpenGlContext instance = new OpenGlContext();

    public static OpenGlContext getInstance() {
        GL.createCapabilities();
        enableFunctionality();
        if ( !OpenGlSupportChecker.isSupportEnough() ) {
            throw new RuntimeException( "OpenGL not supported enough to run this engine!" );
        }
        return instance;
    }
    
    private static void enableFunctionality() {
        if(Boolean.getBoolean( System.getProperty( "debugMode" ) ) ){
            GL11.glEnable( GL_DEBUG_OUTPUT );
        }
        GL11.glEnable( GL11.GL_CULL_FACE );
        GL11.glCullFace( GL11.GL_BACK );
        GL11.glEnable( GL11.GL_BLEND );
        GL11.glEnable( GL11.GL_TEXTURE_2D );
        GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
        GL11.glFrontFace( GL11.GL_CCW );
        ARBVertexArrayObject.glBindVertexArray( ARBVertexArrayObject.glGenVertexArrays() );
    }
    
    private OpenGlContext(){}
    
    public static void terminate() {
        instance = null;
    }
    
    public static void clearScreen() {
        GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
    }
    
    public static void updateViewPort( int x, int y, int width, int height ) {
        GL11.glViewport( x, y, width, height );
    }
}
