package com.firststory.firstoracle.key;

interface KeyCompatibility {
    
    default boolean isCompatible( KeyAction action ) {
        return this == KeyAction.ANY || action == KeyAction.ANY || this == action;
    }
}
