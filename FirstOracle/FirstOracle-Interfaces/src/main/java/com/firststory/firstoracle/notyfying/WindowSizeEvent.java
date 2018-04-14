/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class WindowSizeEvent {
    
    private final WindowContext source;
    private final int width;
    private final int height;
    
    public WindowContext getSource() {
        return source;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public WindowSizeEvent( WindowContext source, int width, int height ) {
        this.source = source;
        this.width = width;
        this.height = height;
    }
}
