/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

/**
 * @author n1t4chi
 */
interface InputActionCompatibility {
    
    default boolean isCompatible( InputAction action ) {
        return this == InputAction.ANY || action == InputAction.ANY || this == action;
    }
}
