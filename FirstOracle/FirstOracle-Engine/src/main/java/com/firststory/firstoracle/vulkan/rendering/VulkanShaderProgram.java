/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.DataBuffer;
import com.firststory.firstoracle.shader.ShaderProgram;
import com.firststory.firstoracle.vulkan.ShaderType;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.buffer.VulkanBufferProvider;
import com.firststory.firstoracle.vulkan.buffer.VulkanDataBuffer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import java.io.IOException;
import java.util.*;

import static com.firststory.firstoracle.vulkan.rendering.VulkanShader.SHADER_FILE_PATH_FRAGMENT;
import static com.firststory.firstoracle.vulkan.rendering.VulkanShader.SHADER_FILE_PATH_VERTEX_3D;

/**
 * @author n1t4chi
 */
public class VulkanShaderProgram implements ShaderProgram {
    
    private static final int UNIFORM_SIZE = FirstOracleConstants.SIZE_MATRIX_4F;
    private static final int UNIFORM_DATA_SIZE = FirstOracleConstants.SIZE_FLOAT * UNIFORM_SIZE;
    private final VulkanShader fragmentShader;
    private final VulkanBufferProvider bufferLoader;
    private final VulkanShader vertexShader;
    private final List< VkPipelineShaderStageCreateInfo > shaderStages = new ArrayList<>();
    private final VulkanPhysicalDevice device;
    
    private final List< VulkanDataBuffer > uniformBuffers = new ArrayList<>( 50 );
    private final Deque< VulkanDataBuffer > uniformBuffersIterator  = new LinkedList<>();
    
    private final float[] uniformBufferData = new float[ UNIFORM_SIZE ];
    private Matrix4fc camera = null;
    private boolean cameraChanged = true;
    
    VulkanShaderProgram(
        VulkanPhysicalDevice device,
        VulkanBufferProvider bufferLoader
    ) {
        this.device = device;
        vertexShader = new VulkanShader( device, SHADER_FILE_PATH_VERTEX_3D, ShaderType.VERTEX );
        fragmentShader = new VulkanShader( device, SHADER_FILE_PATH_FRAGMENT, ShaderType.FRAGMENT );
        this.bufferLoader = bufferLoader;
    }
    
    private VkDescriptorBufferInfo createDescriptorBufferInfo( VulkanDataBuffer uniformBuffer ) {
        return VkDescriptorBufferInfo.create()
            .buffer( uniformBuffer.getBufferAddress().getValue() )
            .offset( uniformBuffer.getMemoryOffset() )
            .range( UNIFORM_DATA_SIZE );
    }
    
    void resetUniformData() {
        uniformBuffersIterator.clear();
        uniformBuffersIterator.addAll( uniformBuffers );
    }
    
    VkDescriptorBufferInfo bindUniformData() {
        var buffer = uniformBuffersIterator.poll();
        if( buffer == null ) {
            buffer = bufferLoader.createUniformBuffer( uniformBufferData );
            uniformBuffers.add( buffer );
        } else {
            buffer.load( uniformBufferData );
        }
        return createDescriptorBufferInfo( buffer );
    }
    
    Matrix4fc getCamera() {
        cameraChanged = false;
        return camera;
    }
    
    boolean didCameraChange() {
        return cameraChanged;
    }
    
    void putUniformData( Matrix4fc camera ) {
        this.camera = new Matrix4f( camera );
        cameraChanged = true;
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
        uniformBuffers.forEach( DataBuffer::close );
        uniformBuffers.clear();
        uniformBuffersIterator.clear();
        vertexShader.dispose();
        fragmentShader.dispose();
        shaderStages.clear();
    }
    
    public List< VkPipelineShaderStageCreateInfo > getShaderStages() {
        return shaderStages;
    }
    
    void clearValues() {
        Arrays.fill( uniformBufferData, 1 );
    }
}

