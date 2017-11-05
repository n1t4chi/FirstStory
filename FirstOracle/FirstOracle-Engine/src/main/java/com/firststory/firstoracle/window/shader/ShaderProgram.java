/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.io.IOException;
import java.util.HashMap;

import static com.firststory.firstoracle.util.IOUtilities.readTextResource;

/**
 * ShaderProgram utility class, new instance creates ready to use program with loaded shaders.
 *
 * @author n1t4chi
 */
public abstract class ShaderProgram {

    private static String shaderTypeToString( int type ) {
        switch ( type ) {
            case GL20.GL_VERTEX_SHADER:
                return "Vertex Shader";
            case GL20.GL_FRAGMENT_SHADER:
                return "Vertex Shader";
            case GL32.GL_GEOMETRY_SHADER:
                return "Geometry Shader";
            default:
                return "Other Shader";
        }
    }

    private final String vertexFilePath;
    private final String fragmentFilePath;
    private final HashMap< String, UniformLocation > uniformLocations = new HashMap<>( 6 );
    private int vertexShader;
    private int fragmentShader;
    private int program;

    public ShaderProgram( String vertexFilePath, String fragmentFilePath ) {
        this.vertexFilePath = vertexFilePath;
        this.fragmentFilePath = fragmentFilePath;
    }

    public void useProgram() {
        GL20.glUseProgram( program );
    }

    public void compile() throws IOException {

        String VertexContent = readTextResource( vertexFilePath );
        String FragmentContent = readTextResource( fragmentFilePath );

        vertexShader = compileSource( GL20.GL_VERTEX_SHADER, VertexContent );
        fragmentShader = compileSource( GL20.GL_FRAGMENT_SHADER, FragmentContent );

        program = GL20.glCreateProgram();
        checkCreatedProgram();

        linkProgram();
        initUniformLocations();
    }

    private void checkCreatedProgram() {
        if ( program < 1 ) {
            throw new ShaderException(
                "Could not create program, check OpenGL support" + "\nvertex:" + vertexFilePath +
                "\nfragment:" + fragmentFilePath );
        }
    }

    public void dispose() {
        GL20.glDeleteProgram( program );
    }

    protected abstract void initUniformLocations();

    protected UniformLocation createUniformLocation( String locationName ) {
        if ( !uniformLocations.containsKey( locationName ) ) {
            UniformLocation uniformLocation = new UniformLocation( program, locationName );
            uniformLocations.put( locationName, uniformLocation );
            return uniformLocation;
        } else {
            throw new ShaderException( "Uniform location already created:" + locationName );
        }
    }

    private void linkProgram() {
        GL20.glAttachShader( program, vertexShader );
        GL20.glAttachShader( program, fragmentShader );
        try {
            GL20.glLinkProgram( program );
            checkLinkingOfProgram();
        } finally {
            GL20.glDetachShader( program, vertexShader );
            GL20.glDetachShader( program, fragmentShader );

            GL20.glDeleteShader( vertexShader );
            GL20.glDeleteShader( fragmentShader );
        }
    }

    private void checkLinkingOfProgram() {
        String err = GL20.glGetProgramInfoLog( program );
        if ( err != null && !err.isEmpty() ) {
            System.err.println(
                "Error after program creation for shaders" + "\nvertex:" + vertexFilePath +
                "\nfragment:" + fragmentFilePath + "\nerror:" + err );
        }
    }

    private int compileSource( int type, String source ) {
        int shader;
        if ( ( shader = GL20.glCreateShader( type ) ) == 0 ) {
            throw new RuntimeException( "Shader creation failed, check OpenGL support." );
        }
        GL20.glShaderSource( shader, source );
        GL20.glCompileShader( shader );

        int comp_stat = GL20.glGetShaderi( shader, GL20.GL_COMPILE_STATUS );
        if ( comp_stat == GL11.GL_FALSE ) {
            String err = GL20.glGetShaderInfoLog( shader );
            throw new RuntimeException(
                "Shader creation failed.\nLog:" + err + "\ntype: " + shaderTypeToString( type ) );
        }
        return shader;
    }

    public class ShaderException extends RuntimeException {

        public ShaderException( String s ) {super( s );}
    }
}
