/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class MouseScrollEvent {
    
    public final WindowContext source;
    public final double xoffset;
    public final double yoffset;
    
    public MouseScrollEvent( WindowContext source, double xoffset, double yoffset ) {
        this.source = source;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
    }
}
