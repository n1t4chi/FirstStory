/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.object2D.PositionableObject2D;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RenderedObjects2D {
    
    Collection< PositionableObject2D > getObjects();
}