/*
 * Copyright (c) 2021 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.exceptions.CannotAllocateVulkanImageMemoryException;
import com.firststory.firstoracle.vulkan.exceptions.CannotBindVulkanImageMemoryException;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;

import java.util.concurrent.atomic.AtomicLong;

class VulkanImageLinearMemory {
    private static final int MEMORY_SIZE_128M = 128 /*MB*/ * 1024 /*kB*/ * 1024 /*B*/;
    
    private final VulkanPhysicalDevice device;
    private final VulkanAddress address;
    private final AtomicLong offsetCounter = new AtomicLong( 0 );
    
    public VulkanImageLinearMemory( VulkanPhysicalDevice device, VulkanMemoryType memoryType ) {
        this.device = device;
        
        var allocateInfo = VkMemoryAllocateInfo
            .calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO )
            .allocationSize( MEMORY_SIZE_128M )
            .memoryTypeIndex( memoryType.getIndex() );
        
        address = VulkanHelper.createAddress( address -> VK10.vkAllocateMemory(
                device.getLogicalDevice(),
                allocateInfo,
                null,
                address
            ),
            resultCode -> new CannotAllocateVulkanImageMemoryException( device, resultCode )
        );
    }
    
    public void bind( VulkanAddress image, VkMemoryRequirements memoryRequirements ) {
        VulkanHelper.assertCallOrThrow( () -> VK10.vkBindImageMemory( device.getLogicalDevice(),
            image.getValue(),
            address.getValue(),
            allocateAndGetOffset( memoryRequirements )
        ), resultCode -> new CannotBindVulkanImageMemoryException( device, resultCode ) );
    }
    
    private long allocateAndGetOffset( VkMemoryRequirements requirements ) {
        var size = requirements.size();
        var alignment = requirements.alignment();
        synchronized ( this ) {
            var currentOffset = offsetCounter.get();
            var missingOffset = ( alignment - (currentOffset % alignment ) ) % alignment;
            var returnedOffset = offsetCounter.addAndGet( missingOffset );
            //shift offset to the end
            offsetCounter.addAndGet( size );
            return returnedOffset;
        }
    }
    
    public void disposeUnsafe() {
        System.out.println( "disposing vulkan linear memory" );
        VK10.vkFreeMemory( device.getLogicalDevice(), address.getValue(), null );
        address.setNull();
    }
}
