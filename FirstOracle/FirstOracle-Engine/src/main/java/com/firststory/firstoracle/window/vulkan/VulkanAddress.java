/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.vulkan.VK10;

/**
 * @author n1t4chi
 */
class VulkanAddress {
    
    static final VulkanAddress NULL = new VulkanAddress();
    private static final long NULL_HANDLE = VK10.VK_NULL_HANDLE;
    private long value;
    
    VulkanAddress( long value ) {
        this.value = value;
    }
    
    VulkanAddress() {
        this( NULL_HANDLE );
    }
    
    @Override
    public int hashCode() {
        return ( int ) ( value ^ ( value >>> 32 ) );
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        
        VulkanAddress that = ( VulkanAddress ) o;
        
        return value == that.value;
    }
    
    VulkanAddress setNull() {
        value = NULL_HANDLE;
        return this;
    }
    
    long getValue() {
        return value;
    }
    
    VulkanAddress setAddress( long value ) {
        this.value = value;
        return this;
    }
    
    boolean isNull() {
        return value == NULL_HANDLE;
    }
}
