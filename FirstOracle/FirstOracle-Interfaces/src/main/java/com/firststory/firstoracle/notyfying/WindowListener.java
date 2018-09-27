/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

/**
 * @author n1t4chi
 */
public interface WindowListener {
    default void notify( WindowSizeEvent event ){}
    default void notify( WindowPositionEvent event ){}
    default void notify( WindowCloseEvent event ){}
    default void notify( WindowFocusedEvent event ){}
}
