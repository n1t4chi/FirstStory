/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.transfer;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanQueueFamily;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanBufferMemory;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommand;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandPool;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanTransferCommandPool extends VulkanCommandPool {
    
    private final List< VulkanBufferMemory > memories = new ArrayList<>();
    private final List< TransferData > allDatas = new ArrayList<>();
    private final Deque< TransferData > availableDatas = new LinkedList<>();
    private final List< TransferData > datasToTransfer = new ArrayList<>();
    private final List< VulkanCommand< VulkanTransferCommandBuffer > > allCommands = new ArrayList<>(  );
    private volatile boolean execute = false;
    
    public VulkanTransferCommandPool(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily
    ) {
        super( allocator, device, usedQueueFamily );
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
        synchronized ( datasToTransfer ) {
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
    }
    
    public void executeQueueLater( VulkanCommand< VulkanTransferCommandBuffer > commands ) {
        synchronized ( allCommands ) {
            allCommands.add( commands );
        }
    }
    
    public void executeTransfers() {
        synchronized ( this ) {
            if( !execute ) {
                executeQueue();
            }
        }
    }
    
    @Override
    protected void dispose( VulkanDeviceAllocator allocator ) {
        allocator.deregisterTransferCommandPool( this );
    }
    
    private void executeQueue() {
        synchronized ( this ) {
            execute = true;
            notifyAll();
            var buffer = createNewCommandBuffer();
            buffer.fillQueueSetup();
    
            executeCommands( buffer );
            executeTransferDatas( buffer );
    
            buffer.fillQueueTearDown();
            getUsedQueueFamily().submit( createSubmitInfo( buffer ) );
            getUsedQueueFamily().waitForQueue();
            memories.forEach( VulkanBufferMemory::resetLocalTransferBuffers );
            execute = false;
        }
    }
    
    private VkSubmitInfo createSubmitInfo( VulkanCommandBuffer commandBuffer ) {
        return VkSubmitInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
            .pCommandBuffers( MemoryUtil.memAllocPointer( 1 )
                .put( 0, commandBuffer.getAddress().getValue() )
            )
        ;
    }
    
    private void executeCommands( VulkanTransferCommandBuffer buffer ) {
        synchronized ( allCommands ) {
            allCommands.forEach( command -> command.execute( buffer ) );
            allCommands.clear();
        }
    }
    private void executeTransferDatas( VulkanTransferCommandBuffer buffer ) {
        synchronized ( datasToTransfer ) {
            datasToTransfer.forEach( data -> data.execute( buffer ) );
            datasToTransfer.clear();
            availableDatas.clear();
            availableDatas.addAll( allDatas );
        }
    }
    
    private VulkanTransferCommandBuffer createNewCommandBuffer() {
        return new VulkanTransferCommandBuffer( getDevice(),
            new VulkanAddress( createPrimaryCommandBufferBuffer( 1 ).get( 0 ) ),
            this,
            VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT
        );
    }
    
}
