/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class WindowFocusedEvent {
    
    public final WindowContext source;
    public final boolean focused;
    
    public WindowFocusedEvent( WindowContext source, boolean focused ) {
        this.source = source;
        this.focused = focused;
    }
}
