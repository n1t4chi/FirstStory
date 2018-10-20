/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.transfer;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferCopy;

/**
 * @author n1t4chi
 */
class TransferData {
    
    private static int id_t = 0;
    private final VulkanAddress source;
    private final long sourceOffset;
    private final VulkanAddress destination;
    private final long destinationOffset;
    private final long length;
    private final int id;
    private final VkBufferCopy region = VkBufferCopy.create();
    private final VkBufferCopy.Buffer regions = VkBufferCopy.create( 1 ).put( 0, region );
    
    TransferData(
        VulkanAddress source,
        long sourceOffset,
        VulkanAddress destination,
        long destinationOffset,
        long length
    ) {
        id = id_t++;
        this.source = source;
        this.sourceOffset = sourceOffset;
        this.destination = destination;
        this.destinationOffset = destinationOffset;
        this.length = length;
    }
    
    @Override
    public String toString() {
        return "TD@" + id + "{" +
            "src=" + source +
            ", srcOfst=" + sourceOffset +
            ", dst=" + destination +
            ", dstOfst=" + destinationOffset +
            ", length=" + length +
        '}';
    }
    
    void execute( VulkanTransferCommandBuffer commandBuffer ) {
        if ( !( source.isNotNull() && destination.isNotNull() ) ) {
            System.err.println( "skip: " + this );
            return;
        }
        System.err.println( "execute:  " + this );
    
        
        VK10.vkCmdCopyBuffer(
            commandBuffer.getCommandBuffer(),
            source.getValue(),
            destination.getValue(),
            regions.put( 0, region
                .srcOffset( sourceOffset )
                .dstOffset( destinationOffset )
                .size( length )
            )
        );
    }
}
