/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.transfer;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.VulkanQueueFamily;
import com.firststory.firstoracle.vulkan.buffer.VulkanBufferMemory;
import com.firststory.firstoracle.vulkan.commands.VulkanCommand;
import com.firststory.firstoracle.vulkan.commands.VulkanCommandPool;
import org.lwjgl.vulkan.VK10;

import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanTransferCommandPool extends VulkanCommandPool< VulkanTransferCommandBuffer > {
    
    private final List< VulkanBufferMemory > memories = new ArrayList<>();
    private final List< TransferData > transferDatas = new ArrayList<>();
    
    public VulkanTransferCommandPool( VulkanPhysicalDevice device, VulkanQueueFamily usedQueueFamily ) {
        super( device, usedQueueFamily );
    }
    
    public void registerMemory( VulkanBufferMemory memory ) {
        memories.add( memory );
    }
    
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
        memories.forEach( VulkanBufferMemory::resetLocalTransferBuffers );
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
