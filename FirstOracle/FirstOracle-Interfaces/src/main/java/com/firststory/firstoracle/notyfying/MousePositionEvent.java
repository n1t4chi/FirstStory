/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class MousePositionEvent {
    
    public final WindowContext source;
    public final double xpos;
    public final double ypos;
    
    public MousePositionEvent( WindowContext source, double xpos, double ypos ) {
        this.source = source;
        this.xpos = xpos;
        this.ypos = ypos;
    }
}
