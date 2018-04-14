/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class WindowCloseEvent {
    
    public final WindowContext source;
    
    public WindowCloseEvent( WindowContext source ) {
        this.source = source;
    }
}
