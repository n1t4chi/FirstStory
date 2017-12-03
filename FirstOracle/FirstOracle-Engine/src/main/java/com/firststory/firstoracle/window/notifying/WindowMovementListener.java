/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.notifying;

/**
 * @author n1t4chi
 */
public interface WindowMovementListener {
    
    void notify( int newX, int newY, WindowMovementNotifier source );
}
