/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.FrameworkProvider;

/**
 * @author n1t4chi
 */
public interface WindowFrameworkProvider extends FrameworkProvider {
    
    WindowFramework getWindowFramework();
    
    boolean isGlfw();
}

