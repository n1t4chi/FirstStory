/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.shader;

import java.io.IOException;
import java.util.HashMap;

import com.firststory.firstoracle.camera.camera3D.Camera3D;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

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

    private final String vertex_file_path;
    private final String fragment_file_path;
    private final HashMap< String, UniformLocation > uniformLocations = new HashMap<>( 6 );
    private int vertexShader;
    private int fragmentShader;
    private int program;

    /**
     * Additional constructor, compiles given shader files and creates program ready to use.
     *
     * @param vertex_file_path
     * @param fragment_file_path
     * @throws IOException
     */
    public ShaderProgram( String vertex_file_path, String fragment_file_path ) {
        this.vertex_file_path = vertex_file_path;
        this.fragment_file_path = fragment_file_path;
    }

    public void useProgram() {
        GL20.glUseProgram( program );
    }

    public void compile() throws IOException {
        // Create the shaders

        // Read the Vertex Shader code from the file
        String VertexContent = readTextResource( vertex_file_path );
        String FragmentContent = readTextResource( fragment_file_path );

        // Compile Vertex Shader
        vertexShader = compileSource( GL20.GL_VERTEX_SHADER, VertexContent );

        // Compile Fragment Shader
        fragmentShader = compileSource( GL20.GL_FRAGMENT_SHADER, FragmentContent );

        // Create program
        program = GL20.glCreateProgram();
        if ( program < 1 ) {
            throw new ShaderException(
                "Could not create program, check OpenGL support" + "\nvertex:" + vertex_file_path +
                "\nfragment:" + fragment_file_path );
        }

        // Link the program
        GL20.glAttachShader( program, vertexShader );
        GL20.glAttachShader( program, fragmentShader );
        try {
            GL20.glLinkProgram( program );
            // Check the program
            String err = GL20.glGetProgramInfoLog( program );
            if ( err != null && !err.isEmpty() ) {
                System.err.println(
                    "Error after program creation for shaders" + "\nvertex:" + vertex_file_path +
                    "\nfragment:" + fragment_file_path + "\nerror:" + err );
            }
        } finally {
            GL20.glDetachShader( program, vertexShader );
            GL20.glDetachShader( program, fragmentShader );

            GL20.glDeleteShader( vertexShader );
            GL20.glDeleteShader( fragmentShader );
        }
        initUniformLocations();
    }

    /**
     * Disposes this object resources. No operation should be performed after dispose().
     */
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

    /**
     * Compiles given shader source.
     *
     * @param type   type of a shader
     * @param source shader source
     * @return shader
     */
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
