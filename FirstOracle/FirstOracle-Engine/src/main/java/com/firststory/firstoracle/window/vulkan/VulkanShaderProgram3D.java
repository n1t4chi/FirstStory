/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.camera3D.Camera3D;
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
    
    private static final int SIZE_FLOAT = 4;
    private static final int SIZE_MATRIX_4F = 4 * 4;
    private static final int SIZE_VEC_4F = 4;
    
    private static final int UNIFORM_SIZE = SIZE_MATRIX_4F + SIZE_VEC_4F * 5;
    private static final int UNIFORM_DATA_SIZE = SIZE_FLOAT * UNIFORM_SIZE;
    
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
    }
    
    void bindUniformData() {
    
        matrix.identity();
        //.scale( ThreadLocalRandom.current().nextFloat() / 10 + 1 );;
        // .rotateZ( rotate = rotate + 0.001f );
        matrix.get( uniformBufferData, 0 );
        uniformBufferData[16] = 0 ; //trans.x
        uniformBufferData[17] = 0 ; //trans.y
        uniformBufferData[18] = 0 ; //trans.z
    
        uniformBufferData[20] = 1 ; //scale.x
        uniformBufferData[21] = 1 ; //scale.y
        uniformBufferData[22] = 1 ; //scale.z
    
        uniformBufferData[24] = 0 ; //rotation.x
        uniformBufferData[25] = 0 ; //rotation.y
        uniformBufferData[26] = 90 ; //rotation.z
    
        uniformBufferData[28] = 0 ; //overlayColour.x
        uniformBufferData[29] = 1 ; //overlayColour.y
        uniformBufferData[30] = 0 ; //overlayColour.z
        uniformBufferData[31] = 0.5f ; //overlayColour.w
    
        uniformBufferData[32] = 0.5f ; //maxAlphaChannel
        uniformBuffer.load( uniformBufferData );
    }
    
    @Override
    public void bindPosition( Vector3fc vector ) {
    
    }
    
    @Override
    public void bindCamera( Camera3D camera3D ) {
    
    }
    
    @Override
    public void bindCamera( Matrix4fc camera ) {
    
    }
    
    @Override
    public void bindScale( Vector3fc vector ) {
    
    }
    
    @Override
    public void bindRotation( Vector3fc vector ) {
    
    }
    
    @Override
    public void bindOverlayColour( Vector4fc vector ) {
    
    }
    
    @Override
    public void bindMaxAlphaChannel( float value ) {
    
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
    
    VkDescriptorBufferInfo createBufferInfo() {
        return VkDescriptorBufferInfo.create()
            .buffer( uniformBuffer.getBufferAddress().getValue() )
            .offset( 0 )
            .range( UNIFORM_DATA_SIZE );
    }
    
    List< VkPipelineShaderStageCreateInfo > getShaderStages() {
        return shaderStages;
    }
}
