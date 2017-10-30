/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle.window.shader;

import java.io.IOException;
import java.util.HashMap;

import com.firststory.firstoracle.Camera;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import static com.firststory.util.IOUtilities.readTextResource;

/**
 * ShaderProgram utility class, new instance creates ready to use program with loaded shaders.
 *
 * @author n1t4chi
 */
public class ShaderProgram {

    private final static String UNIFORM_LOCATION_POSITION = "translation";
    private final static String UNIFORM_LOCATION_CAMERA = "camera";
    private final static String UNIFORM_LOCATION_SCALE = "scale";
    private final static String UNIFORM_LOCATION_ROTATION = "rotation";
    private final static String UNIFORM_LOCATION_OVERLAY_COLOUR = "overlayColour";
    private final static String UNIFORM_LOCATION_MAX_ALPHA_CHANNEL = "maxAlphaChannel";
    private static final String VERTEX_SHADER_FILE_PATH =
        "First Oracle/shader.vert";
    private static final String FRAGMENT_SHADER_FILE_PATH =
        "First Oracle/shader.frag";

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
    private UniformLocation positionLocation;
    private UniformLocation cameraLocation;
    private UniformLocation scaleLocation;
    private UniformLocation rotationLocation;
    private UniformLocation overlayColourLocation;
    private UniformLocation maxAlphaChannelLocation;
    /**
     * Default constructor, compiles given shader files and creates program ready to use.
     */
    public ShaderProgram() {
        this.vertex_file_path = VERTEX_SHADER_FILE_PATH;
        this.fragment_file_path = FRAGMENT_SHADER_FILE_PATH;
    }

    /**
     * Additional constructor, compiles given shader files and creates program ready to use.
     *
     *
     * @param vertex_file_path
     * @param fragment_file_path
     * @throws IOException
     */
    public ShaderProgram( String vertex_file_path, String fragment_file_path ) {
        this.vertex_file_path = vertex_file_path;
        this.fragment_file_path = fragment_file_path;
    }

    public void bindPosition(Vector3fc vector) {
        positionLocation.bind( vector );
    }

    public void bindCamera(Camera camera) {
        cameraLocation.bind( camera );
    }

    public void bindCamera(Matrix4fc camera) {
        cameraLocation.bind( camera );
    }

    public void bindScale(Vector3fc vector) {
        scaleLocation.bind( vector );
    }

    public void bindRotation(Vector3fc vector) {
        rotationLocation.bind( vector );
    }
        //"./FirstStory/resources/First Oracle/shader.frag";

    public void bindOverlayColour(Vector4fc vector) {
        overlayColourLocation.bind( vector );
    }

    public void bindMaxAlphaChannel(float value) {
        maxAlphaChannelLocation.bind( value );
    }

    public void compile() throws IOException {
        // Create the shaders

        // Read the Vertex Shader code from the file
        String VertexContent = readTextResource(vertex_file_path);
        String FragmentContent = readTextResource(fragment_file_path);

        // Compile Vertex Shader
        vertexShader = compileSource( GL20.GL_VERTEX_SHADER, VertexContent );

        // Compile Fragment Shader
        fragmentShader = compileSource( GL20.GL_FRAGMENT_SHADER, FragmentContent );

        // Create program
        program = GL20.glCreateProgram();
        if ( program < 1 ) {
            throw new ShaderException( "Could not create program, check OpenGL support"
                                       + "\nvertex:" + vertex_file_path
                                       + "\nfragment:" + fragment_file_path
            );
        }

        // Link the program
        GL20.glAttachShader( program, vertexShader );
        GL20.glAttachShader( program, fragmentShader );
        try {
            GL20.glLinkProgram( program );
            GL20.glUseProgram( program );
            // Check the program
            String err = GL20.glGetProgramInfoLog( program );
            if ( err != null && !err.isEmpty() ) {
                System.err.println( "Error after program creation for shaders"
                                    + "\nvertex:" + vertex_file_path
                                    + "\nfragment:" + fragment_file_path
                                    + "\nerror:" + err
                );
            }
        }
        finally {
            GL20.glDetachShader( program, vertexShader );
            GL20.glDetachShader( program, fragmentShader );

            GL20.glDeleteShader( vertexShader );
            GL20.glDeleteShader( fragmentShader );
        }
        positionLocation = createUniformLocation( UNIFORM_LOCATION_POSITION );
        scaleLocation = createUniformLocation( UNIFORM_LOCATION_SCALE );
        cameraLocation = createUniformLocation( UNIFORM_LOCATION_CAMERA );
        rotationLocation = createUniformLocation( UNIFORM_LOCATION_ROTATION );
        overlayColourLocation = createUniformLocation( UNIFORM_LOCATION_OVERLAY_COLOUR );
        maxAlphaChannelLocation = createUniformLocation( UNIFORM_LOCATION_MAX_ALPHA_CHANNEL );
    }

    /**
     * Disposes this object resources. No operation should be performed after dispose().
     */
    public void dispose() {
        GL20.glDeleteProgram( program );
    }

    private UniformLocation createUniformLocation( String locationName )
    {
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
            throw new RuntimeException( "Shader creation failed.\nLog:" + err + "\ntype: " +
                                        shaderTypeToString( type ) );
        }
        return shader;
    }

    public class ShaderException extends RuntimeException {

        public ShaderException( String s ) {super( s );}
    }
}
