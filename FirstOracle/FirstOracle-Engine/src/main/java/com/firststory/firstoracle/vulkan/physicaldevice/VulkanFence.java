/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanFenceException;
import com.firststory.firstoracle.vulkan.exceptions.VulkanFenceExecutionException;
import com.firststory.firstoracle.vulkan.exceptions.VulkanFenceExecutionTimeoutException;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkFenceCreateInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author n1t4chi
 */
public class VulkanFence {
    private static final long NANOS_IN_MICRO = 1000;
    private static final long MICROS_IN_MILI = 1000;
    private static final long MILIS_IN_SECOND = 1000;
    private static final long SECONDS_IN_MINUTE = 60;
    private static final long MINUTE_IN_NANO_SECONDS = SECONDS_IN_MINUTE * MILIS_IN_SECOND * MICROS_IN_MILI * NANOS_IN_MICRO;
    private final VulkanDeviceAllocator allocator;
    private final VulkanPhysicalDevice device;
    private final VulkanAddress address;
    private final ExecutorService executorService;
    private List< Future< ? > > futures = new ArrayList<>();
    
    public VulkanFence( VulkanDeviceAllocator allocator, VulkanPhysicalDevice device ) {
        this.allocator = allocator;
        this.device = device;
        address = createFence();
        executorService = device.getEventExecutorService();
    }
    
    public VulkanAddress getAddress() {
        return address;
    }
    
    public void update() {
        VK10.vkResetFences( device.getLogicalDevice(), address.getValue() );
    }
    
    public void dispose() {
        allocator.deregisterFence( this );
    }
    
    public void disposeUnsafe() {
        futures.forEach( future -> {
            if( !future.isDone() ) {
                future.cancel( true );
            }
        } );
        VK10.vkDestroyFence( device.getLogicalDevice(), address.getValue(), null );
    }
    
    public void executeWhenFinishedThenDispose( Runnable runnable ) {
        futures.add( executorService.submit( () -> {
            var result = VK10.vkWaitForFences( device.getLogicalDevice(), address.getValue(), true, MINUTE_IN_NANO_SECONDS );
            switch ( result ) {
                case VK10.VK_SUCCESS:
                    runnable.run();
                    dispose();
                    break;
                case VK10.VK_TIMEOUT:
                    throw new VulkanFenceExecutionTimeoutException( device, this );
                default:
                    throw new VulkanFenceExecutionException( device, this, result );
            }
        } ) );
    }
    
    private VulkanAddress createFence() {
        return VulkanHelper.createAddress( () -> VkFenceCreateInfo.create()
                .sType( VK10.VK_STRUCTURE_TYPE_FENCE_CREATE_INFO )
                .flags( FirstOracleConstants.NO_FLAGS )
                .pNext( VK10.VK_NULL_HANDLE )
            ,
            ( createInfo, address ) -> VK10.vkCreateFence( device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanFenceException( device, resultCode )
        );
    }
    
    @Override
    public String toString() {
        return "VulkanFence@" + address;
    }
}
