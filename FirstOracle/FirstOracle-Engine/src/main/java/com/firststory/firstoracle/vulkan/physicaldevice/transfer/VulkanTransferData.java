/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.transfer;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
public class VulkanTransferData {
    
    private static int id_t = 0;
    private VulkanAddress source;
    private long sourceOffset;
    private VulkanAddress destination;
    private long destinationOffset;
    private long length;
    private int id;
    private final VkBufferCopy region = VkBufferCopy.calloc();
    private final VkBufferCopy.Buffer regions = VkBufferCopy.calloc( 1 ).put( 0, region );
    
    public VulkanTransferData() {
        id = id_t++;
    }
    
    public VulkanTransferData set(
        VulkanAddress source,
        long sourceOffset,
        VulkanAddress destination,
        long destinationOffset,
        long length
    ) {
        this.source = source;
        this.sourceOffset = sourceOffset;
        this.destination = destination;
        this.destinationOffset = destinationOffset;
        this.length = length;
        return this;
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
        if ( source.isNull() || destination.isNull() ) {
            return;
        }
        VK10.vkCmdCopyBuffer(
            commandBuffer.getCommandBuffer(),
            source.getValue(),
            destination.getValue(),
            regions.put(
                0,
                region
                    .srcOffset( sourceOffset )
                    .dstOffset( destinationOffset )
                    .size( length )
            )
        );
    }
}
