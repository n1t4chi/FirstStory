/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.data.Rotation;
import com.firststory.firstoracle.data.Scale;
import com.firststory.firstoracle.shader.ShaderProgram;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.buffer.VulkanBufferProvider;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public class VulkanShaderProgram3D extends VulkanShaderProgram implements ShaderProgram {
    
    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_SCALE = 4;
    private static final int OFFSET_ROTATION = 8;
    private static final int OFFSET_COLOUR = 12;
    private static final int OFFSET_ALPHA_CHANNEL = 16;
    
    private static final int INPUT_SIZE = FirstOracleConstants.SIZE_VEC_4F * 5 + FirstOracleConstants.SIZE_INT;
    private final float[] inBufferData = new float[ INPUT_SIZE ];
    
    public VulkanShaderProgram3D( VulkanPhysicalDevice device, VulkanBufferProvider bufferLoader ) {
        super( device, bufferLoader );
        clearValues();
    }
    
    void bindCamera( Matrix4fc camera ) {
        putUniformData( camera );
    }
    
    float[] getInputData() {
        return inBufferData;
    }
    
    void bindPosition( Vector3fc vector ) {
        putInputData( OFFSET_POSITION, vector.x(), vector.y(), vector.z() );
    }
    
    void bindPosition( Vector2fc vector ) {
        putInputData( OFFSET_POSITION, vector.x(), vector.y(), 0 );
    }
    
    void bindPosition( Position position ) {
        putInputData( OFFSET_POSITION, position.x(), position.y(), position.z() );
    }
    
    void bindScale( Vector2fc vector ) {
        putInputData( OFFSET_SCALE, vector.x(), vector.y(), 0 );
    }
    
    void bindScale( Vector3fc vector ) {
        putInputData( OFFSET_SCALE, vector.x(), vector.y(), vector.z() );
    }
    
    void bindScale( Scale scale ) {
        putInputData( OFFSET_SCALE, scale.x(), scale.y(), scale.z() );
    }
    
    void bindRotation( float rotation ) {
        putInputData( OFFSET_ROTATION, 0 , 0, rotation );
    }
    
    void bindRotation( Vector3fc vector ) {
        putInputData( OFFSET_ROTATION, vector.x(), vector.y(), vector.z() );
    }
    
    void bindRotation( Rotation rotation ) {
        putInputData( OFFSET_ROTATION, rotation.ox(), rotation.oy(), rotation.oz() );
    }
    
    void bindOverlayColour( Colour vector ) {
        putInputData( OFFSET_COLOUR, vector.red(), vector.green(), vector.blue(), vector.alpha() );
    }
    
    void bindMaxAlphaChannel( float value ) {
        putInputData( OFFSET_ALPHA_CHANNEL, value );
    }
    
    private void putInputData( int offset, float... data ) {
        System.arraycopy( data, 0, inBufferData, offset, data.length );
    }
}
