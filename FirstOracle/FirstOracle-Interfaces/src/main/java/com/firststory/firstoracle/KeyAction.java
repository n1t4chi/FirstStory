package com.firststory.firstoracle;

public enum KeyAction implements Compatible {
    PRESS, REPEAT, UNKNOWN, RELEASE, ANY
}

interface Compatible {
    
    default boolean isCompatible( KeyAction action ) {
        return this == KeyAction.ANY || action == KeyAction.ANY || this == action;
    }
}


