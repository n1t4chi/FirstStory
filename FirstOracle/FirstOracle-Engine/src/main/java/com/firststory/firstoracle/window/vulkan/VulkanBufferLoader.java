/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.ArrayBufferLoader;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanVertexBuffer;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferCreateInfo;

/**
 * @author n1t4chi
 */
public class VulkanBufferLoader implements ArrayBufferLoader {
    
    static final float[] POSITION = new float[]{
        0.0f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f
    };
    static final float[] COLOUR = new float[]{
        1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f
    };
    
    private final VulkanPhysicalDevice device;
    private VulkanAddress address;
    
    VulkanBufferLoader( VulkanPhysicalDevice device ) {
        
        this.device = device;
    }
    
    @Override
    public int create() throws CannotCreateBufferException {
        return 1;
    }
    
    @Override
    public void bind( int bufferID ) {
    
    }
    
    @Override
    public void load( int bufferID, float[] bufferData ) {
    
    }
    
    @Override
    public void delete( int bufferID ) {
    
    }
    
    @Override
    public void close() throws Exception {
    
    }
    
    int getVertexLength() {
        return COLOUR.length + POSITION.length;
    }
    
    void update() {
    
        this.address = createBuffer();
    }
    
    private VulkanAddress createBuffer() {
        return VulkanHelper.createAddress(
            () -> VkBufferCreateInfo.create()
                .sType( VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO )
                .size( 4 * getVertexLength() )
                .usage( VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT )
                .sharingMode( VK10.VK_SHARING_MODE_EXCLUSIVE ),
            ( createInfo, address ) -> VK10.vkCreateBuffer( device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanVertexBuffer( device, resultCode )
        );
    }
}
