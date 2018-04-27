/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

/**
 * @author n1t4chi
 */
class VulkanException extends RuntimeException {
    
    VulkanException( String s, Exception ex ) {
        super( s, ex );
    }
    
    VulkanException() {
    }
    
    VulkanException( String s ) {
        super( s );
    }
}
