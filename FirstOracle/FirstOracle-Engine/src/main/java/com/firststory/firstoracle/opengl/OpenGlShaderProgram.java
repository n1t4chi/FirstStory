/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.opengl;

import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.data.Rotation;
import com.firststory.firstoracle.data.Scale;
import com.firststory.firstoracle.shader.ShaderException;
import com.firststory.firstoracle.shader.ShaderProgram;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.io.IOException;
import java.util.HashMap;

import static com.firststory.firstoracle.FirstOracleConstants.SHADER_FILES_LOCATION;
import static com.firststory.firstoracle.templates.IOUtilities.readTextResource;

/**
 * @author n1t4chi
 */
class OpenGlShaderProgram implements ShaderProgram {
    
    private final static String UNIFORM_LOCATION_POSITION = "translation";
    private final static String UNIFORM_LOCATION_CAMERA = "camera";
    private final static String UNIFORM_LOCATION_SCALE = "scale";
    private final static String UNIFORM_LOCATION_ROTATION = "rotation";
    private final static String UNIFORM_LOCATION_OVERLAY_COLOUR = "overlayColour";
    private final static String UNIFORM_LOCATION_MAX_ALPHA_CHANNEL = "maxAlphaChannel";
    private static final String VERTEX_SHADER_FILE_PATH = SHADER_FILES_LOCATION + "shader3D.gl.vert";
    private static final String FRAGMENT_SHADER_FILE_PATH = SHADER_FILES_LOCATION + "shader.gl.frag";
    
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
    private final HashMap< String, OpenGlUniformLocation > uniformLocations = new HashMap<>( 6 );
    
    private OpenGlUniformLocation positionLocation;
    private OpenGlUniformLocation cameraLocation;
    private OpenGlUniformLocation scaleLocation;
    private OpenGlUniformLocation rotationLocation;
    private OpenGlUniformLocation overlayColourLocation;
    private OpenGlUniformLocation maxAlphaChannelLocation;
    private int vertexShader;
    private int fragmentShader;
    private int program;
    
    OpenGlShaderProgram() {
        this.vertexFilePath = VERTEX_SHADER_FILE_PATH;
        this.fragmentFilePath = FRAGMENT_SHADER_FILE_PATH;
    }
    
    OpenGlShaderProgram( String vertex_file_path, String fragment_file_path ) {
        this.vertexFilePath = vertex_file_path;
        this.fragmentFilePath = fragment_file_path;
    }
    
    @Override
    public void useProgram() {
        GL20.glUseProgram( program );
    }
    
    @Override
    public void compile() throws IOException {
    
        var VertexContent = readTextResource( vertexFilePath );
        var FragmentContent = readTextResource( fragmentFilePath );
        
        vertexShader = compileSource( GL20.GL_VERTEX_SHADER, VertexContent );
        fragmentShader = compileSource( GL20.GL_FRAGMENT_SHADER, FragmentContent );
        
        program = GL20.glCreateProgram();
        checkCreatedProgram();
        
        linkProgram();
        initUniformLocations();
    }
    
    @Override
    public void dispose() {
        GL20.glDeleteProgram( program );
    }
    
    void bindCamera( Matrix4fc camera ) {
        cameraLocation.bind( camera );
    }
    
    void bindPosition( Position position ) {
        positionLocation.bind( position );
    }
    
    void bindScale( Scale scale ) {
        scaleLocation.bind( scale );
    }
    
    void bindRotation( Rotation rotation ) {
        rotationLocation.bind( rotation );
    }
    
    void bindOverlayColour( Colour colour ) {
        overlayColourLocation.bind( colour );
    }
    
    void bindMaxAlphaChannel( float value ) {
        maxAlphaChannelLocation.bind( value );
    }
    
    OpenGlUniformLocation createUniformLocation( String locationName ) {
        if ( !uniformLocations.containsKey( locationName ) ) {
            var uniformLocation = new OpenGlUniformLocation( program, locationName );
            uniformLocations.put( locationName, uniformLocation );
            return uniformLocation;
        } else {
            throw new ShaderException( "Uniform location already created:" + locationName );
        }
    }
    
    protected void initUniformLocations() {
        positionLocation = createUniformLocation( UNIFORM_LOCATION_POSITION );
        scaleLocation = createUniformLocation( UNIFORM_LOCATION_SCALE );
        rotationLocation = createUniformLocation( UNIFORM_LOCATION_ROTATION );
        overlayColourLocation = createUniformLocation( UNIFORM_LOCATION_OVERLAY_COLOUR );
        maxAlphaChannelLocation = createUniformLocation( UNIFORM_LOCATION_MAX_ALPHA_CHANNEL );
        cameraLocation = createUniformLocation( UNIFORM_LOCATION_CAMERA );
    }
    
    private int compileSource( int type, String source ) {
        int shader;
        if ( ( shader = GL20.glCreateShader( type ) ) == 0 ) {
            throw new RuntimeException( "Shader creation failed, check OpenGL support." );
        }
        GL20.glShaderSource( shader, source );
        GL20.glCompileShader( shader );
    
        var comp_stat = GL20.glGetShaderi( shader, GL20.GL_COMPILE_STATUS );
        if ( comp_stat == GL11.GL_FALSE ) {
            var err = GL20.glGetShaderInfoLog( shader );
            throw new RuntimeException(
                "Shader creation failed.\nLog:" + err + "\ntype: " + shaderTypeToString( type ) );
        }
        return shader;
    }
    
    private void checkCreatedProgram() {
        if ( program < 1 ) {
            throw new ShaderException(
                "Could not create program, check OpenGL support" + "\nvertex:" + vertexFilePath +
                    "\nfragment:" + fragmentFilePath );
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
        var err = GL20.glGetProgramInfoLog( program );
        if ( !err.isEmpty() ) {
            System.err.println(
                "Error after program creation for shaders" + "\nvertex:" + vertexFilePath +
                    "\nfragment:" + fragmentFilePath + "\nerror:" + err );
        }
    }
}
