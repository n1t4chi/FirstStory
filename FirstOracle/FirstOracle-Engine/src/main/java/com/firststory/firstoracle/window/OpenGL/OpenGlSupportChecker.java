/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.OpenGL;

import org.lwjgl.opengl.GLCapabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.lwjgl.opengl.GL.getCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author n1t4chi
 */
public class OpenGlSupportChecker {
    
    private static List<CapabilityTester > tests = new ArrayList<>(  );

    static {
        tests.add( test( c -> c.OpenGL11, "opengl 1.1" ) );
        tests.add( test( c -> c.OpenGL15, "opengl 1.5" ) );
        tests.add( test( c -> c.OpenGL20, "opengl 2.0" ) );
        tests.add( test( c -> c.OpenGL30, "opengl 3.0" ) );
        //tests.add( test( c -> c.OpenGL33, "opengl 3.3" ) );
        tests.add( test( c -> c.GL_ARB_shader_objects, "Shader objects" ) );
        tests.add( test( c -> c.GL_ARB_vertex_shader, "Vertex shader" ) );
        tests.add( test( c -> c.GL_ARB_fragment_shader, "Fragment shader" ) );
        tests.add( testFunction( c -> c.glCreateShader, "glCreateShader" ) );
        tests.add( testFunction( c -> c.glShaderSource, "glShaderSource" ) );
        tests.add( testFunction( c -> c.glCompileShader, "glCompileShader" ) );
        tests.add( testFunction( c -> c.glGetShaderiv, "glGetShaderiv" ) );
        tests.add( testFunction( c -> c.glCreateProgram, "glCreateProgram" ) );
        tests.add( testFunction( c -> c.glAttachShader, "glAttachShader" ) );
        tests.add( testFunction( c -> c.glLinkProgram, "glLinkProgram" ) );
        tests.add( testFunction( c -> c.glGetProgramInfoLog, "glGetProgramInfoLog" ) );
        tests.add( testFunction( c -> c.glLinkProgram, "glLinkProgram" ) );
        tests.add( testFunction( c -> c.glDetachShader, "glDetachShader" ) );
        tests.add( testFunction( c -> c.glDeleteShader, "glDeleteShader" ) );
        tests.add( testFunction( c -> c.glDeleteProgram, "glDeleteProgram" ) );
        tests.add( testFunction( c -> c.glDepthMask, "glDepthMask" ) );
        tests.add( testFunction( c -> c.glEnable, "glEnable" ) );
        tests.add( testFunction( c -> c.glDepthFunc, "glDepthFunc" ) );
        tests.add( testFunction( c -> c.glClear, "glClear" ) );
        tests.add( testFunction( c -> c.glDisable, "glDisable" ) );
        tests.add( testFunction( c -> c.glCullFace, "glCullFace" ) );
        tests.add( testFunction( c -> c.glBlendFunc, "glBlendFunc" ) );
        tests.add( testFunction( c -> c.glFrontFace, "glFrontFace" ) );
        tests.add( testFunction( c -> c.glClearColor, "glClearColor" ) );
        tests.add( testFunction( c -> c.glUseProgram, "glUseProgram" ) );
        tests.add( testFunction( c -> c.glEnableVertexAttribArray, "glEnableVertexAttribArray" ) );
        tests.add( testFunction( c -> c.glBindBuffer, "glBindBuffer" ) );
        tests.add( testFunction( c -> c.glUniform1fv, "glUniform1fv" ) );
        tests.add( testFunction( c -> c.glVertexAttribPointer, "glVertexAttribPointer" ) );
        tests.add( testFunction( c -> c.glDrawArrays, "glDrawArrays" ) );
        tests.add( testFunction( c -> c.glLineWidth, "glLineWidth" ) );
        tests.add( testFunction( c -> c.glGetUniformLocation, "glGetUniformLocation" ) );
        tests.add( testFunction( c -> c.glUniformMatrix4fv, "glUniformMatrix4fv" ) );
        tests.add( testFunction( c -> c.glBindBuffer, "glBindBuffer" ) );
        tests.add( testFunction( c -> c.glBufferData, "glBufferData" ) );
        tests.add( testFunction( c -> c.glGenBuffers, "glGenBuffers" ) );
        tests.add( testFunction( c -> c.glDeleteBuffers, "glDeleteBuffers" ) );
        tests.add( testFunction( c -> c.glDeleteTextures, "glDeleteTextures" ) );
        tests.add( testFunction( c -> c.glGenTextures, "glGenTextures" ) );
        tests.add( testFunction( c -> c.glBindTexture, "glBindTexture" ) );
        tests.add( testFunction( c -> c.glTexImage2D, "glTexImage2D" ) );
        tests.add( testFunction( c -> c.glTexParameteri, "glTexParameteri" ) );
        tests.add( testFunction( c -> c.glGenerateMipmap, "glGenerateMipmap" ) );
    }
    
    public static boolean validate() throws OpenGlNotSupported{
        GLCapabilities capabilities = getCapabilities();
        tests.forEach( capabilityTester -> capabilityTester.checkSupport( capabilities ) );
        return true;
    }

    private static CapabilityTester test( Predicate< GLCapabilities > test, String name ){
        return () -> new CapabilityTest( test, name );
    }
    
    private static CapabilityTester testFunction( Function<GLCapabilities, Long> function, String name ){
        return () -> new CapabilityTest( c -> function.apply( c ) != NULL, "Function "+name );
    }
    
    private interface CapabilityTester {
        CapabilityTest support();
        default void checkSupport(GLCapabilities capabilities) throws OpenGlNotSupported {
            CapabilityTest support = support();
            if( !support.test( capabilities ) ){
                throw new OpenGlNotSupported(support.capabilityName+" not supported");
            }
        }
    }
    
    private static class CapabilityTest {
        final Predicate<GLCapabilities> test;
        final String capabilityName;
        public CapabilityTest( Predicate< GLCapabilities > test, String capabilityName ) {
            this.test = test;
            this.capabilityName = capabilityName;
        }
        public boolean test( GLCapabilities capabilities ) {
            return test.test( capabilities );
        }
    }
}
