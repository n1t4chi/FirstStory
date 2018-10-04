/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import java.util.Collections;
import java.util.List;

/**
 * @author n1t4chi
 */
public class DummyGrid2D implements Grid2D {
    
    public static final DummyGrid2D DUMMY_GRID_2D = new DummyGrid2D();
    
    @Override
    public List< RenderData > toRenderDataList() {
        return Collections.emptyList();
    }
    
    @Override
    public void dispose() {
    }
}
