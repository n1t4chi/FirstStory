/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.notifying;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface QuitListener {
    
    /**
     * Notifies listening context that it should stop working.
     * Listener should provide reference to all threads that possibly could still be running after method returns.
     * Notifier should respect and wait for all of them to finish work.
     * @param event quit event
     * @param source source that started quit event
     * @return collection of references to possibly working threads
     */
    Collection< Thread > notify( QuitEvent event, QuitNotifier source );
}
