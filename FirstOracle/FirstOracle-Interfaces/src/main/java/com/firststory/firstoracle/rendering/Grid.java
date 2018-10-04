/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import java.util.List;

/**
 * @author n1t4chi
 */
public interface Grid {
    List< RenderData > toRenderDataList();
    
    void dispose();
}
