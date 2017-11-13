/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle;

import org.lwjgl.opengl.GLCapabilities;

import static org.lwjgl.opengl.GL.getCapabilities;

/**
 * @author n1t4chi
 */
public class CheckSupport {

    public static boolean checkSupport() {
        boolean proceed = true;
        GLCapabilities capabilities = getCapabilities();
        if ( !capabilities.OpenGL11 ) {
            System.err.println( "opengl 1.1 not supported" );
            proceed = false;
        }
        if ( !capabilities.OpenGL15 ) {
            System.err.println( "opengl 1.5 not supported" );
            proceed = false;
        }
        if ( !capabilities.OpenGL20 ) {
            System.err.println( "opengl 2.0 not supported" );
            proceed = false;
        }
        if ( !capabilities.OpenGL30 ) {
            System.err.println( "opengl 3.0 not supported" );
            proceed = false;
        }
        if ( !capabilities.GL_ARB_shader_objects ) {
            System.err.println( "Shader objects not supported" );
            proceed = false;
        }
        if ( !capabilities.GL_ARB_vertex_shader ) {
            System.err.println( "Vertex shader not supported" );
            proceed = false;
        }
        if ( !capabilities.GL_ARB_fragment_shader ) {
            System.err.println( "Fragment shader not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glCreateShader ) {
            System.err.println( "Function glCreateShader not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glShaderSource ) {
            System.err.println( "Function glShaderSource not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glCompileShader ) {
            System.err.println( "Function glCompileShader not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glGetShaderiv ) {
            System.err.println( "Function glGetShaderiv not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glCreateProgram ) {
            System.err.println( "Function glCreateProgram not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glAttachShader ) {
            System.err.println( "Function glAttachShader not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glLinkProgram ) {
            System.err.println( "Function glLinkProgram not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glGetProgramInfoLog ) {
            System.err.println( "Function glGetProgramInfoLog not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glLinkProgram ) {
            System.err.println( "Function glLinkProgram not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glDetachShader ) {
            System.err.println( "Function glDetachShader not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glDeleteShader ) {
            System.err.println( "Function glDeleteShader not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glDeleteProgram ) {
            System.err.println( "Function glDeleteProgram not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDepthMask ) {
            System.err.println( "Function glDepthMask not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glEnable ) {
            System.err.println( "Function glEnable not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDepthFunc ) {
            System.err.println( "Function glDepthFunc not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glClear ) {
            System.err.println( "Function glClear not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDisable ) {
            System.err.println( "Function glDisable not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glCullFace ) {
            System.err.println( "Function glCullFace not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glBlendFunc ) {
            System.err.println( "Function glBlendFunc not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glFrontFace ) {
            System.err.println( "Function glFrontFace not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glClearColor ) {
            System.err.println( "Function glClearColor not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glUseProgram ) {
            System.err.println( "Function glUseProgram not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glEnableVertexAttribArray ) {
            System.err.println(
                "Function glEnableVertexAttribArray not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glBindBuffer ) {
            System.err.println( "Function glBindBuffer not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glUniform1fv ) {
            System.err.println( "Function glUniform1fv not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glVertexAttribPointer ) {
            System.err.println( "Function glVertexAttribPointer not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDrawArrays ) {
            System.err.println( "Function glDrawArrays not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glLineWidth ) {
            System.err.println( "Function glLineWidth not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glGetUniformLocation ) {
            System.err.println( "Function glGetUniformLocation not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glUniformMatrix4fv ) {
            System.err.println( "Function glUniformMatrix4fv not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glBindBuffer ) {
            System.err.println( "Function glBindBuffer not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glBufferData ) {
            System.err.println( "Function glBufferData not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glGenBuffers ) {
            System.err.println( "Function glGenBuffers not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDeleteBuffers ) {
            System.err.println( "Function glDeleteBuffers not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDeleteTextures ) {
            System.err.println( "Function glDeleteTextures not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glGenTextures ) {
            System.err.println( "Function glGenTextures not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glBindTexture ) {
            System.err.println( "Function glBindTexture not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glTexImage2D ) {
            System.err.println( "Function glTexImage2D not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glTexParameteri ) {
            System.err.println( "Function glTexParameteri not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glGenerateMipmap ) {
            System.err.println( "Function glGenerateMipmap not supported" );
            proceed = false;
        }

        return proceed;
    }
}
