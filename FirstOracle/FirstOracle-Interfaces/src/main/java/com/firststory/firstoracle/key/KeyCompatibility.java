/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.key;

/**
 * @author n1t4chi
 */
interface KeyCompatibility {
    
    default boolean isCompatible( KeyAction action ) {
        return this == KeyAction.ANY || action == KeyAction.ANY || this == action;
    }
}
