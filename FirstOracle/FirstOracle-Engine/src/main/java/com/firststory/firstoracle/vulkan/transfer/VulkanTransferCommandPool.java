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
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanTransferCommandPool extends VulkanCommandPool< VulkanTransferCommandBuffer > {
    
    private final List< VulkanBufferMemory > memories = new ArrayList<>();
    private final List< TransferData > allDatas = new ArrayList<>();
    private final Deque< TransferData > availableDatas = new LinkedList<>();
    private final List< TransferData > datasToTransfer = new ArrayList<>();
    
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
        var transferData = availableDatas.poll();
        if( transferData == null ) {
            transferData = new TransferData();
            allDatas.add( transferData );
        }
        datasToTransfer.add( transferData.set(
            source,
            sourceOffset,
            destination,
            destinationOffset,
            length
        ) );
    }
    
    public void executeTransfers() {
        executeQueue( buffer -> {} );
    }
    
    public void executeQueue( VulkanCommand< VulkanTransferCommandBuffer > commands ) {
        var buffer = createNewCommandBuffer();
        buffer.fillQueueSetup();
        
        executeTransferDatas( buffer );
        commands.execute( buffer );
        
        buffer.fillQueueTearDown();
        submitQueue( buffer );
        executeTearDown();
        memories.forEach( VulkanBufferMemory::resetLocalTransferBuffers );
    }
    
    private void executeTransferDatas( VulkanTransferCommandBuffer buffer ) {
        datasToTransfer.forEach( data -> data.execute( buffer ) );
        datasToTransfer.clear();
        availableDatas.clear();
        availableDatas.addAll( allDatas );
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
