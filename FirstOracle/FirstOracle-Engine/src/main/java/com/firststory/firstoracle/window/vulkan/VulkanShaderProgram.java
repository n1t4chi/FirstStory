/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.shader.ShaderProgram;
import org.joml.Matrix4fc;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.firststory.firstoracle.window.vulkan.VulkanShader.SHADER_FILE_PATH_FRAGMENT;
import static com.firststory.firstoracle.window.vulkan.VulkanShader.SHADER_FILE_PATH_VERTEX_3D;

/**
 * @author n1t4chi
 */
public class VulkanShaderProgram implements ShaderProgram {
    
    private static final int UNIFORM_SIZE = FirstOracleConstants.SIZE_MATRIX_4F;
    private static final int INPUT_SIZE = FirstOracleConstants.SIZE_VEC_4F * 5 + FirstOracleConstants.SIZE_INT;
    private static final int UNIFORM_DATA_SIZE = FirstOracleConstants.SIZE_FLOAT * UNIFORM_SIZE;
    private final VulkanShader fragmentShader;
    private final VulkanShader vertexShader;
    private final List< VkPipelineShaderStageCreateInfo > shaderStages = new ArrayList<>();
    private final VulkanPhysicalDevice device;
    private final VulkanUniformBuffer uniformBuffer;
    
    private final float[] uniformBufferData = new float[ UNIFORM_SIZE ];
    private final float[] inBufferData = new float[ INPUT_SIZE ];
    
    VulkanShaderProgram(
        VulkanPhysicalDevice device, VulkanDataBufferProvider bufferLoader
    ) {
        this.device = device;
        vertexShader = new VulkanShader( device, SHADER_FILE_PATH_VERTEX_3D, ShaderType.VERTEX );
        fragmentShader = new VulkanShader( device, SHADER_FILE_PATH_FRAGMENT, ShaderType.FRAGMENT );
        uniformBuffer = bufferLoader.createUniformBuffer( UNIFORM_SIZE, FirstOracleConstants.SIZE_FLOAT );
    }
    
    void bindUniformData( VulkanAddress descriptorSet, VulkanGraphicCommandBuffer commandBuffer ) {
        uniformBuffer.load( uniformBufferData );
        commandBuffer.bindDescriptorSets( descriptorSet );
    }
    
    float[] getInputData(  ) {
        return inBufferData;
    }
    
    void putInputData( int offset, float... data ) {
        System.arraycopy( data, 0, inBufferData, offset, data.length );
    }
    
    void putUniformData( Matrix4fc camera ) {
        camera.get( uniformBufferData );
    }
    
    @Override
    public void useProgram() {}
    
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
    
    VkDescriptorBufferInfo createBufferInfo() {
        return VkDescriptorBufferInfo.create()
            .buffer( uniformBuffer.getBufferAddress().getValue() )
            .offset( 0 )
            .range( UNIFORM_DATA_SIZE );
    }
    
    List< VkPipelineShaderStageCreateInfo > getShaderStages() {
        return shaderStages;
    }
    
    void clearValues() {
        Arrays.fill( uniformBufferData, 1 );
    }
}

