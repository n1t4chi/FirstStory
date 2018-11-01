/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.transfer;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.allocators.VulkanCommandBufferAllocator;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanQueueFamily;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanSemaphore;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommand;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandPool;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanTransferCommandPool extends VulkanCommandPool {
    private final VulkanCommandBufferAllocator< VulkanTransferCommandBuffer > bufferAllocator;
    
    private final List< VulkanTransferData > datasToTransfer = new ArrayList<>();
    private final List< VulkanCommand< VulkanTransferCommandBuffer > > allCommands = new ArrayList<>(  );
    
    public VulkanTransferCommandPool(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily
    ) {
        super( allocator, device, usedQueueFamily );
        bufferAllocator = allocator.createBufferAllocator( this::createNewCommandBuffer );
    }
    
    public void addTransferData(
        VulkanAddress source,
        long sourceOffset,
        VulkanAddress destination,
        long destinationOffset,
        long length
    ) {
        synchronized ( datasToTransfer ) {
            datasToTransfer.add( getAllocator().createTransferData(
                source,
                sourceOffset,
                destination,
                destinationOffset,
                length
            ) );
        }
    }
    
    public void addTransferCommands( VulkanCommand< VulkanTransferCommandBuffer > commands ) {
        synchronized ( allCommands ) {
            allCommands.add( commands );
        }
    }
    
    private class Holder< T > {
        T value;
        
        void hold( T newValue ) {
            value = newValue;
        }
        
        T get() {
            return value;
        }
        
        void remove() {
            value = null;
        }
    }
    
    private final Holder< VulkanSemaphore > currentSemaphore = new Holder<>();
    
    public void executeTransfersAndForget() {
        var semaphore = execute();
        semaphore.ignoreWait();
    }
    
    public VulkanSemaphore executeTransfers() {
        return execute();
    }
    
    private VulkanSemaphore execute() {
        synchronized ( currentSemaphore ) {
            var semaphore = currentSemaphore.get();
            if( semaphore == null ) {
                currentSemaphore.hold( semaphore = getAllocator().createSemaphore() );
                currentSemaphore.notifyAll();
                executeTransferUnsafe( semaphore );
                currentSemaphore.remove();
            }
            return semaphore;
        }
    }
    
    @Override
    public void disposeUnsafe() {
        bufferAllocator.dispose();
        super.disposeUnsafe();
    }
    
    @Override
    protected void dispose( VulkanDeviceAllocator allocator ) {
        allocator.deregisterTransferCommandPool( this );
    }
    
    private void executeTransferUnsafe( VulkanSemaphore semaphore ) {
        ArrayList<VulkanCommand<VulkanTransferCommandBuffer>> allCommandsCopy;
        List<VulkanTransferData> datasToTransferCopy;
        synchronized ( allCommands ) {
            allCommandsCopy = new ArrayList<>( allCommands );
            allCommands.clear();
        }
        synchronized ( datasToTransfer ) {
            datasToTransferCopy = new ArrayList<>( datasToTransfer );
            datasToTransfer.clear();
        }
        
        var buffer = bufferAllocator.createBuffer();
        buffer.fillQueueSetup();
        allCommandsCopy.forEach( command -> command.execute( buffer ) );
        datasToTransferCopy.forEach( data -> data.execute( buffer ) );
        
        buffer.fillQueueTearDown();
        var fence = getAllocator().createFence();
        getUsedQueueFamily().submit( fence, createSubmitInfo( buffer, semaphore ) );
        fence.executeWhenFinishedThenDispose( () -> {
            buffer.dispose();
            semaphore.finishedSignal();
        } );
    }
    
    private VkSubmitInfo createSubmitInfo(
        VulkanTransferCommandBuffer commandBuffer,
        VulkanSemaphore semaphore
    ) {
        var submitInfo = VkSubmitInfo.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
            .pCommandBuffers( MemoryUtil.memAllocPointer( 1 )
                .put( 0, commandBuffer.getAddress().getValue() )
            );
        if( semaphore != null ) {
            submitInfo.pSignalSemaphores( MemoryUtil.memAllocLong( 1 )
                .put( 0, semaphore.getAddressForSignal().getValue() )
            );
        }
        return submitInfo;
    }
    
    private VulkanTransferCommandBuffer createNewCommandBuffer() {
        return new VulkanTransferCommandBuffer(
            bufferAllocator,
            getDevice(),
            this,
            new VulkanAddress( createPrimaryCommandBufferBuffer( 1 ).get( 0 ) ),
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT
        );
    }
    
}
