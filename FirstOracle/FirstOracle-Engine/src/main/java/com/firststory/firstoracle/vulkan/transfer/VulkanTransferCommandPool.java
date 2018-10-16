/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.transfer;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.VulkanQueueFamily;
import com.firststory.firstoracle.vulkan.commands.VulkanCommand;
import com.firststory.firstoracle.vulkan.commands.VulkanCommandPool;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferCopy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanTransferCommandPool extends VulkanCommandPool< VulkanTransferCommandBuffer > {
    public VulkanTransferCommandPool( VulkanPhysicalDevice device, VulkanQueueFamily usedQueueFamily ) {
        super( device, usedQueueFamily );
    }
    
    private static class TransferData {
        private final VulkanAddress source;
        private final long sourceOffset;
        private final VulkanAddress destination;
        private final long destinationOffset;
        private final long length;
    
        private static int id_t=0;
        private final int id;
        public TransferData(
            VulkanAddress source,
            long sourceOffset,
            VulkanAddress destination,
            long destinationOffset,
            long length
        ) {
            id = id_t ++;
            this.source = source;
            this.sourceOffset = sourceOffset;
            this.destination = destination;
            this.destinationOffset = destinationOffset;
            this.length = length;
        }
    
        private void execute( VulkanTransferCommandBuffer commandBuffer ) {
            if( !( source.isNotNull() && destination.isNotNull() ) ) {
                System.err.println( "skip: "+this );
                return;
            }
            System.err.println( "execute:  "+this );
            var copyRegion = VkBufferCopy.create()
                .srcOffset( sourceOffset )
                .dstOffset( destinationOffset )
                .size( length );
    
            VK10.vkCmdCopyBuffer(
                commandBuffer.getCommandBuffer(),
                source.getValue(),
                destination.getValue(),
                VkBufferCopy.create( 1 ).put( 0, copyRegion )
            );
        }
    
        @Override
        public String toString() {
            return "TD@" + id+ "{" +
                "src=" + source +
                ", srcOfst=" + sourceOffset +
                ", dst=" + destination +
                ", dstOfst=" + destinationOffset +
                ", length=" + length +
            '}';
        }
    }
    
    private final List< TransferData > transferDatas = new ArrayList<>();
    
    public void putDataToTransferForLater(
        VulkanAddress source,
        long sourceOffset,
        VulkanAddress destination,
        long destinationOffset,
        long length
    ) {
        var transferData = new TransferData( source, sourceOffset, destination, destinationOffset, length );
        transferDatas.add( transferData );
        System.err.println( "add:  " + transferData + ", size:" + transferDatas.size() );
    }
    
    public void putDataToTransferForNow(
        VulkanAddress source,
        long sourceOffset,
        VulkanAddress destination,
        long destinationOffset,
        long length
    ) {
        putDataToTransferForLater( source, sourceOffset, destination, destinationOffset, length );
        executeTransfers();
    }
    
    public void executeTransfers() {
        executeQueue( buffer -> {} );
    }
    
    public void executeQueue( VulkanCommand< VulkanTransferCommandBuffer > commands ) {
        var buffer = createNewCommandBuffer();
        buffer.fillQueueSetup();
    
        executeTransferDatas( buffer );
        commands.execute( buffer );
        System.err.println( "end transfer" );
        
        buffer.fillQueueTearDown();
        submitQueue( buffer );
        executeTearDown();
    }
    
    private void executeTransferDatas( VulkanTransferCommandBuffer buffer ) {
        transferDatas.forEach( data -> data.execute( buffer ) );
        transferDatas.clear();
    }
    
    private VulkanTransferCommandBuffer createNewCommandBuffer() {
        return new VulkanTransferCommandBuffer( getDevice(),
            new VulkanAddress( createPrimaryCommandBufferBuffer( 1 ).get( 0 ) ),
            this,
            VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT
        );
    }
    
    private void executeTearDown() {
        getUsedQueueFamily().waitForQueue();
    }
}
