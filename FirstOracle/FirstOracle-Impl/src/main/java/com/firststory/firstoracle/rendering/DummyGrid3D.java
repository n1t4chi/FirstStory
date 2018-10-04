/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import java.util.Collections;
import java.util.List;

/**
 * @author n1t4chi
 */
public class DummyGrid3D implements Grid3D {
    
    public static final DummyGrid3D DUMMY_GRID_3D = new DummyGrid3D();
    
    @Override
    public List< RenderData > toRenderDataList() {
        return Collections.emptyList();
    }
    
    @Override
    public void dispose() {
    }
}
