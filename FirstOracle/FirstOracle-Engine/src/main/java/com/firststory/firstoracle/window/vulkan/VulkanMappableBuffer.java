/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.BufferNotLoadedException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotMapVulkanMemoryException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;

import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
public abstract class VulkanMappableBuffer extends VulkanDataBuffer< ByteBuffer > {
    
    public VulkanMappableBuffer(
        VulkanPhysicalDevice device, int[] usageFlags, int[] requiredMemoryTypeFlags
    )
    {super( device, usageFlags, requiredMemoryTypeFlags );}
    
    @Override
    public void bind() throws BufferNotCreatedException, BufferNotLoadedException {}
    
    @Override
    public void load( ByteBuffer dataBuffer ) throws BufferNotCreatedException {
        copyMemory( dataBuffer );
        unmapMemory();
    }
    
    void unmapMemory() {
        VK10.vkUnmapMemory( getDeviceLogicalDevice(), getAllocatedMemoryAddress().getValue() );
    }
    
    private void copyMemory( ByteBuffer dataBuffer ) {
        MemoryUtil.memCopy( MemoryUtil.memAddress( dataBuffer ), mapMemory().getValue(), dataBuffer.remaining() );
        MemoryUtil.memFree( dataBuffer );
    }
    
    private VulkanAddress mapMemory() {
        return VulkanHelper.createAddressViaBuffer( address -> VK10.vkMapMemory( getDeviceLogicalDevice(),
            getAllocatedMemoryAddress().getValue(),
            0,
            getAllocateInfo().allocationSize(),
            0,
            address
        ), resultCode -> new CannotMapVulkanMemoryException( getDevice(), resultCode ) );
    }
}
