/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.object3D.PositionableObject3D;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RenderedObjects3D {
    
    Collection< PositionableObject3D< ?, ? > > getObjects();
}
