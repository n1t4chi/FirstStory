/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.shader.ShaderProgram2D;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public class VulkanShaderProgram2D extends VulkanShaderProgram implements ShaderProgram2D {
    
    private static final int OFFSET_SCALE = 20;
    private static final int OFFSET_ROTATION = 24;
    private static final int OFFSET_COLOUR = 28;
    private static final int OFFSET_ALPHA_CHANNEL = 32;
    private static final int OFFSET_CAMERA = 0;
    private static final int OFFSET_POSITION = 16;
    
    VulkanShaderProgram2D( VulkanPhysicalDevice device, VulkanDataBufferProvider bufferLoader ) {
        super( device, bufferLoader );
        clearValues();
    }
    
    @Override
    public void bindPosition( Vector2fc vector ) {
        bindData( OFFSET_POSITION, vector.x(), vector.y(), 0 );
    }
    
    @Override
    public void bindCamera( Matrix4fc camera ) {
        bindData( OFFSET_CAMERA, camera );
    }
    
    @Override
    public void bindScale( Vector2fc vector ) {
        bindData( OFFSET_SCALE, vector.x(), vector.y(), 0 );
    }
    
    @Override
    public void bindRotation( float value ) {
        bindData( OFFSET_ROTATION, 0, 0, value );
    }
    
    @Override
    public void bindOverlayColour( Vector4fc vector ) {
        bindData( OFFSET_COLOUR, vector.x(), vector.y(), vector.z(), vector.w() );
    }
    
    @Override
    public void bindMaxAlphaChannel( float value ) {
        bindData( OFFSET_ALPHA_CHANNEL, value );
    }
    
    @Override
    void clearValues() {
        bindCamera( FirstOracleConstants.MATRIX_4F_IDENTIFY );
        bindPosition( FirstOracleConstants.VECTOR_ZERO_2F );
        bindScale( FirstOracleConstants.VECTOR_ONES_2F );
        bindRotation( 0 );
        bindOverlayColour( FirstOracleConstants.VECTOR_ZERO_4F );
        bindMaxAlphaChannel( 1f );
    }
}
