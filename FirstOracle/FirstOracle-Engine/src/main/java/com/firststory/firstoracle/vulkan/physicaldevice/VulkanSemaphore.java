/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanSemaphoreException;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

/**
 * @author n1t4chi
 */
public class VulkanSemaphore {
    
    private final VulkanDeviceAllocator allocator;
    private final VulkanPhysicalDevice device;
    private final VulkanAddress address;
    private boolean usedForSignal = false;
    private boolean signaled = false;
    private boolean usedForWait = false;
    private boolean waited = false;
    private boolean disposed = false;
    
    
    public VulkanSemaphore( VulkanDeviceAllocator allocator, VulkanPhysicalDevice device ) {
        this.allocator = allocator;
        this.device = device;
        this.address = createSemaphore( device );
    }
    
    public void finishedSignal() {
        if( disposed ) {
            throw new RuntimeException( "Semaphore was disposed before" );
        }
        if ( !usedForSignal ) {
            throw new RuntimeException( "Semaphore signaled as finished signaling but not extracted for signal" );
        }
        signaled = true;
        deregister();
    }
    
    public VulkanAddress getAddressForSignal() {
        if( disposed ) {
            throw new RuntimeException( "Semaphore was disposed before" );
        }
        usedForSignal = true;
        return address;
    }
    
    public void finishedWait() {
        if( disposed ) {
            throw new RuntimeException( "Semaphore was disposed before" );
        }
        if( !usedForWait ) {
            throw new RuntimeException( "Semaphore signaled as finished waiting but not extracted for wait" );
        }
        waited = true;
        deregister();
    }
    
    public VulkanAddress getAddressForWait() {
        if( disposed ) {
            throw new RuntimeException( "Semaphore was disposed before" );
        }
        usedForWait = true;
        return address;
    }
    
    void ignoreSignal() {
        usedForSignal = true;
        signaled = true;
        deregister();
    }
    
    public void ignoreWait() {
        usedForWait = true;
        waited = true;
        deregister();
    }
    
    public void disposeUnsafe() {
        if( !disposed ) {
            VK10.vkDestroySemaphore( device.getLogicalDevice(), address.getValue(), null );
            disposed = true;
        } else{
            throw new RuntimeException( "disposed semaphore again!" );
        }
    }
    
    private void deregister() {
        synchronized ( this ) {
            if ( notUsed() || finishedUsed() ) {
                allocator.deregisterSemaphore( this );
            }
        }
    }
    
    private boolean finishedUsed() {
        return usedForSignal && waited && usedForWait && signaled;
    }
    
    private boolean notUsed() {
        return !usedForSignal && !usedForWait;
    }
    
    @Override
    public String toString() {
        return "Semaphore{ " + address + " }";
    }
    
    private VulkanAddress createSemaphore( VulkanPhysicalDevice device ) {
        return VulkanHelper.createAddress(
            address -> VK10.vkCreateSemaphore(
                device.getLogicalDevice(),
                createSemaphoreCreateInfo(),
                null,
                address
            ),
            resultCode -> new CannotCreateVulkanSemaphoreException( device, resultCode )
        );
    }
    
    private VkSemaphoreCreateInfo createSemaphoreCreateInfo() {
        return VkSemaphoreCreateInfo.calloc().sType( VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO );
    }
}
