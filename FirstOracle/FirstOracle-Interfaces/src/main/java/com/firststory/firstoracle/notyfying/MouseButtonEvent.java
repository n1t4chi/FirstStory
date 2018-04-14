/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class MouseButtonEvent {
    
    public final WindowContext source;
    public final int button;
    public final int action;
    public final int mods;
    
    public MouseButtonEvent( WindowContext source, int button, int action, int mods ) {
        this.source = source;
        this.button = button;
        this.action = action;
        this.mods = mods;
    }
}
