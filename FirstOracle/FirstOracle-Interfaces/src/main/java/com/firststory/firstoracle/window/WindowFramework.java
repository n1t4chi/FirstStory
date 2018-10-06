/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.WindowSettings;

/**
 * @author n1t4chi
 */
public interface WindowFramework {
    
    WindowContext createWindowContext( WindowSettings settings, boolean isOpenGLWindow );
    
    double getTime();
    
    void destroy();
}
