package com.firststory.firstoracle.window.notifying;

public interface WindowListener {
    default void notify( WindowSizeEvent event ){}
    default void notify( WindowPositionEvent event ){}
    default void notify( WindowCloseEvent event ){}
    default void notify( WindowFocusedEvent event ){}
}
