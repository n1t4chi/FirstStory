/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import org.lwjgl.vulkan.VK10;

/**
 * @author n1t4chi
 */
public class VulkanAddress {
    
    private static final VulkanAddress NULL = createNull();
    private static final long NULL_HANDLE = VK10.VK_NULL_HANDLE;
    
    public static VulkanAddress createNull() { return new VulkanAddress(); }
    
    private long value;
    
    public VulkanAddress( long value ) {
        this.value = value;
    }
    
    public VulkanAddress() {
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
    
        var that = ( VulkanAddress ) o;
        
        return value == that.value;
    }
    
    public VulkanAddress setNull() {
        value = NULL_HANDLE;
        return this;
    }
    
    public long getValue() {
        return value;
    }
    
    public VulkanAddress setAddress( long value ) {
        this.value = value;
        return this;
    }
    
    public boolean isNotNull() {
        return value != NULL_HANDLE;
    }
    
    @Override
    public String toString() {
        return "Adr=" + value;
    }
}
