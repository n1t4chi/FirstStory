/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.shader.ShaderProgram3D;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.firststory.firstoracle.window.vulkan.VulkanShaderProgram.SHADER_FILE_PATH_FRAGMENT;
import static com.firststory.firstoracle.window.vulkan.VulkanShaderProgram.SHADER_FILE_PATH_VERTEX_3D;

/**
 * @author n1t4chi
 */
public class VulkanShaderProgram3D implements ShaderProgram3D {
    
    protected static final int OFFSET_SCALE = 20;
    protected static final int OFFSET_ROTATION = 24;
    protected static final int OFFSET_COLOUR = 28;
    protected static final int OFFSET_ALPHA_CHANNEL = 32;
    private static final int SIZE_FLOAT = 4;
    private static final int SIZE_MATRIX_4F = 4 * 4;
    private static final int SIZE_VEC_4F = 4;
    private static final int UNIFORM_SIZE = SIZE_MATRIX_4F + SIZE_VEC_4F * 5;
    private static final int UNIFORM_DATA_SIZE = SIZE_FLOAT * UNIFORM_SIZE;
    private static final int OFFSET_CAMERA = 0;
    private static final int OFFSET_POSITION = 16;
    private final VulkanShaderProgram fragmentShader;
    private final VulkanShaderProgram vertexShader;
    private final List< VkPipelineShaderStageCreateInfo > shaderStages = new ArrayList<>();
    private final VulkanPhysicalDevice device;
    private final VulkanUniformBuffer uniformBuffer;
    
    private final float[] uniformBufferData = new float[UNIFORM_SIZE];
    
    private final Matrix4f matrix = new Matrix4f();
    
    VulkanShaderProgram3D( VulkanPhysicalDevice device, VulkanDataBufferProvider bufferLoader ) {
        this.device = device;
        vertexShader = new VulkanShaderProgram( device, SHADER_FILE_PATH_VERTEX_3D, ShaderType.VERTEX );
        fragmentShader = new VulkanShaderProgram( device, SHADER_FILE_PATH_FRAGMENT, ShaderType.FRAGMENT );
        uniformBuffer = bufferLoader.createUniformBuffer( UNIFORM_SIZE, SIZE_FLOAT );
    
        clearValues();
    }
    
    @Override
    public void bindPosition( Vector3fc vector ) {
        uniformBufferData[OFFSET_POSITION] = vector.x();
        uniformBufferData[OFFSET_POSITION + 1] = vector.y();
        uniformBufferData[OFFSET_POSITION + 2] = vector.z();
    }
    
    @Override
    public void bindCamera( Matrix4fc camera ) {
        matrix.get( uniformBufferData, OFFSET_CAMERA );
    }
    
    @Override
    public void bindScale( Vector3fc vector ) {
        uniformBufferData[OFFSET_SCALE] = vector.x();
        uniformBufferData[OFFSET_SCALE + 1] = vector.y();
        uniformBufferData[OFFSET_SCALE + 2] = vector.z();
    }
    
    @Override
    public void bindRotation( Vector3fc vector ) {
        uniformBufferData[OFFSET_ROTATION] = vector.x();
        uniformBufferData[OFFSET_ROTATION + 1] = vector.y();
        uniformBufferData[OFFSET_ROTATION + 2] = vector.z();
    }
    
    @Override
    public void bindOverlayColour( Vector4fc vector ) {
        uniformBufferData[OFFSET_COLOUR] = vector.x();
        uniformBufferData[OFFSET_COLOUR + 1] = vector.y();
        uniformBufferData[OFFSET_COLOUR + 2] = vector.z();
        uniformBufferData[OFFSET_COLOUR + 3] = vector.w();
    }
    
    @Override
    public void bindMaxAlphaChannel( float value ) {
        uniformBufferData[OFFSET_ALPHA_CHANNEL] = value;
    }
    
    @Override
    public void useProgram() {
    
    }
    
    @Override
    public void compile() throws IOException {
        vertexShader.compile();
        fragmentShader.compile();
        shaderStages.add( vertexShader.getStageCreateInfo() );
        shaderStages.add( fragmentShader.getStageCreateInfo() );
    }
    
    @Override
    public void dispose() {
        uniformBuffer.close();
        vertexShader.dispose();
        fragmentShader.dispose();
        shaderStages.clear();
    }
    
    void bindUniformData() {
        uniformBuffer.load( uniformBufferData );
    }
    
    VkDescriptorBufferInfo createBufferInfo() {
        return VkDescriptorBufferInfo.create()
            .buffer( uniformBuffer.getBufferAddress().getValue() )
            .offset( 0 )
            .range( UNIFORM_DATA_SIZE );
    }
    
    List< VkPipelineShaderStageCreateInfo > getShaderStages() {
        return shaderStages;
    }
    
    private void clearValues() {
        bindCamera( FirstOracleConstants.MATRIX_4F_IDENTIFY );
        bindPosition( FirstOracleConstants.VECTOR_ZERO_3F );
        bindScale( FirstOracleConstants.VECTOR_ONES_3F );
        bindRotation( FirstOracleConstants.VECTOR_ZERO_3F );
        bindOverlayColour( FirstOracleConstants.VECTOR_ZERO_4F );
        bindMaxAlphaChannel( 1f );
    }
}
