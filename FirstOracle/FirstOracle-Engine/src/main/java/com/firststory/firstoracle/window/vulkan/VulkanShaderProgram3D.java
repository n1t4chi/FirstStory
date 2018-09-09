/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.shader.ShaderProgram2D;
import com.firststory.firstoracle.shader.ShaderProgram3D;
import com.firststory.firstoracle.window.vulkan.buffer.VulkanBufferProvider;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public class VulkanShaderProgram3D extends VulkanShaderProgram implements ShaderProgram3D , ShaderProgram2D {
    
    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_SCALE = 4;
    private static final int OFFSET_ROTATION = 8;
    private static final int OFFSET_COLOUR = 12;
    private static final int OFFSET_ALPHA_CHANNEL = 16;
    
    VulkanShaderProgram3D( VulkanPhysicalDevice device, VulkanBufferProvider bufferLoader ) {
        super( device, bufferLoader );
        clearValues();
    }
    
    public void bindPosition( Vector3fc vector ) {
        putInputData( OFFSET_POSITION, vector.x(), vector.y(), vector.z() );
    }
    
    public void bindPosition( Vector2fc vector ) {
        putInputData( OFFSET_POSITION, vector.x(), vector.y(), 0 );
    }
    
    public void bindCamera( Matrix4fc camera ) {
        putUniformData( camera );
    }
    
    public void bindScale( Vector2fc vector ) {
        putInputData( OFFSET_SCALE, vector.x(), vector.y(), 0 );
    }
    
    public void bindScale( Vector3fc vector ) {
        putInputData( OFFSET_SCALE, vector.x(), vector.y(), vector.z() );
    }
    
    public void bindRotation( float rotation ) {
        putInputData( OFFSET_ROTATION, 0 , 0, rotation );
    }
    
    public void bindRotation( Vector3fc vector ) {
        putInputData( OFFSET_ROTATION, vector.x(), vector.y(), vector.z() );
    }
    
    public void bindOverlayColour( Vector4fc vector ) {
        putInputData( OFFSET_COLOUR, vector.x(), vector.y(), vector.z(), vector.w() );
    }
    
    public void bindMaxAlphaChannel( float value ) {
        putInputData( OFFSET_ALPHA_CHANNEL, value );
    }
}
