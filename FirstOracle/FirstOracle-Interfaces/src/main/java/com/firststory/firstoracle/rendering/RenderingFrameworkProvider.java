/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FrameworkProvider;
import com.firststory.firstoracle.window.WindowContext;

public interface RenderingFrameworkProvider extends FrameworkProvider {
    
    RenderingFramework getRenderingFramework( WindowContext window );
    
    boolean isOpenGL();
}
