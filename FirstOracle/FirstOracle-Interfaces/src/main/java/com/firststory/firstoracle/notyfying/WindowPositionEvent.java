/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class WindowPositionEvent {
    
    public final WindowContext source;
    public final int xpos;
    public final int ypos;
    
    public WindowPositionEvent( WindowContext source, int xpos, int ypos ) {
        
        this.source = source;
        this.xpos = xpos;
        this.ypos = ypos;
    }
}
