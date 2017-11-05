/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

/**
 * @author: n1t4chi
 */
public interface TimeObserver {
    void notify(double newTimeSnapshot,TimeNotifier source);
}
