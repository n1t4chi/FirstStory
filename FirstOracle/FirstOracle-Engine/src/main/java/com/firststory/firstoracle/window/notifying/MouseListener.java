/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.notifying;

public /**
 * @author n1t4chi
 */
interface MouseListener {
    default void notify( MouseScrollEvent event ){}
    default void notify( MousePositionEvent event ){}
    default void notify( MouseButtonEvent event ){}
}
