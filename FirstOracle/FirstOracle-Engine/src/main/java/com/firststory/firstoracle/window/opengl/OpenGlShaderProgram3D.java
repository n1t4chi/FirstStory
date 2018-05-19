/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.opengl;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.shader.ShaderProgram3D;
import com.firststory.firstoracle.shader.UniformLocation;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public class OpenGlShaderProgram3D extends OpenGlShaderProgram implements ShaderProgram3D {
    
    private final static String UNIFORM_LOCATION_POSITION = "translation";
    private final static String UNIFORM_LOCATION_CAMERA = "camera";
    private final static String UNIFORM_LOCATION_SCALE = "scale";
    private final static String UNIFORM_LOCATION_ROTATION = "rotation";
    private final static String UNIFORM_LOCATION_OVERLAY_COLOUR = "overlayColour";
    private final static String UNIFORM_LOCATION_MAX_ALPHA_CHANNEL = "maxAlphaChannel";
    private static final String VERTEX_SHADER_FILE_PATH = FirstOracleConstants.SHADER_FILES_LOCATION +
        "shader/shader3D.gl.vert";
    private static final String FRAGMENT_SHADER_FILE_PATH = FirstOracleConstants.SHADER_FILES_LOCATION +
        "shader/shader.gl.frag";
    
    private UniformLocation positionLocation;
    private UniformLocation cameraLocation;
    private UniformLocation scaleLocation;
    private UniformLocation rotationLocation;
    private UniformLocation overlayColourLocation;
    private UniformLocation maxAlphaChannelLocation;
    
    public OpenGlShaderProgram3D() {
        super( VERTEX_SHADER_FILE_PATH, FRAGMENT_SHADER_FILE_PATH );
    }
    
    public OpenGlShaderProgram3D( String vertex_file_path, String fragment_file_path ) {
        super( vertex_file_path, fragment_file_path );
    }
    
    @Override
    public void bindPosition( Vector3fc vector ) {
        positionLocation.bind( vector );
    }
    
    @Override
    public void bindCamera( Camera3D camera3D ) {
        cameraLocation.bind( camera3D );
    }
    
    @Override
    public void bindCamera( Matrix4fc camera ) {
        cameraLocation.bind( camera );
    }
    
    @Override
    public void bindScale( Vector3fc vector ) {
        scaleLocation.bind( vector );
    }
    
    @Override
    public void bindRotation( Vector3fc vector ) {
        rotationLocation.bind( vector );
    }
    
    @Override
    public void bindOverlayColour( Vector4fc vector ) {
        overlayColourLocation.bind( vector );
    }
    
    @Override
    public void bindMaxAlphaChannel( float value ) {
        maxAlphaChannelLocation.bind( value );
    }
    
    @Override
    protected void initUniformLocations() {
        positionLocation = createUniformLocation( UNIFORM_LOCATION_POSITION );
        scaleLocation = createUniformLocation( UNIFORM_LOCATION_SCALE );
        rotationLocation = createUniformLocation( UNIFORM_LOCATION_ROTATION );
        overlayColourLocation = createUniformLocation( UNIFORM_LOCATION_OVERLAY_COLOUR );
        maxAlphaChannelLocation = createUniformLocation( UNIFORM_LOCATION_MAX_ALPHA_CHANNEL );
        cameraLocation = createUniformLocation( UNIFORM_LOCATION_CAMERA );
    }
}
