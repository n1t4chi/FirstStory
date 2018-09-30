/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.opengl;

import com.firststory.firstoracle.object.data.*;
import com.firststory.firstoracle.shader.ShaderProgram;
import org.joml.Matrix4fc;

import static com.firststory.firstoracle.FirstOracleConstants.SHADER_FILES_LOCATION;

/**
 * @author n1t4chi
 */
class OpenGlShaderProgram3D extends OpenGlShaderProgram implements ShaderProgram {
    
    private final static String UNIFORM_LOCATION_POSITION = "translation";
    private final static String UNIFORM_LOCATION_CAMERA = "camera";
    private final static String UNIFORM_LOCATION_SCALE = "scale";
    private final static String UNIFORM_LOCATION_ROTATION = "rotation";
    private final static String UNIFORM_LOCATION_OVERLAY_COLOUR = "overlayColour";
    private final static String UNIFORM_LOCATION_MAX_ALPHA_CHANNEL = "maxAlphaChannel";
    private static final String VERTEX_SHADER_FILE_PATH = SHADER_FILES_LOCATION + "shader3D.gl.vert";
    private static final String FRAGMENT_SHADER_FILE_PATH = SHADER_FILES_LOCATION + "shader.gl.frag";
    
    private OpenGlUniformLocation positionLocation;
    private OpenGlUniformLocation cameraLocation;
    private OpenGlUniformLocation scaleLocation;
    private OpenGlUniformLocation rotationLocation;
    private OpenGlUniformLocation overlayColourLocation;
    private OpenGlUniformLocation maxAlphaChannelLocation;
    
    OpenGlShaderProgram3D() {
        super( VERTEX_SHADER_FILE_PATH, FRAGMENT_SHADER_FILE_PATH );
    }
    
    OpenGlShaderProgram3D( String vertex_file_path, String fragment_file_path ) {
        super( vertex_file_path, fragment_file_path );
    }
    
    void bindCamera( Matrix4fc camera ) {
        cameraLocation.bind( camera );
    }
    
    void bindPosition( Position3D vector ) {
        positionLocation.bind( vector );
    }
    
    void bindPosition( Position2D vector ) {
        positionLocation.bind( vector );
    }
    
    void bindScale( Scale3D vector ) {
        scaleLocation.bind( vector );
    }
    
    void bindScale( Scale2D vector ) {
        scaleLocation.bind( vector );
    }
    
    void bindRotation( Rotation3D vector ) {
        rotationLocation.bind( vector );
    }
    
    void bindRotation( Rotation2D rotation ) {
        rotationLocation.bind( rotation );
    }
    
    void bindOverlayColour( Colour vector ) {
        overlayColourLocation.bind( vector );
    }
    
    void bindMaxAlphaChannel( float value ) {
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
