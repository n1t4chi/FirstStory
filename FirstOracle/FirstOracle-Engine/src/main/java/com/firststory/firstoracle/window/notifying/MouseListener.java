package com.firststory.firstoracle.window.notifying;

public interface MouseListener {
    default void notify( MouseScrollEvent event ){}
    default void notify( MousePositionEvent event ){}
    default void notify( MouseButtonEvent event ){}
}
