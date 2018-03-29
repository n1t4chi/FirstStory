/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FrameworkProvider;

public interface RenderingFrameworkProvider extends FrameworkProvider {
    
    RenderingFramework getRenderingContext();
    
    boolean isOpenGL();
}
