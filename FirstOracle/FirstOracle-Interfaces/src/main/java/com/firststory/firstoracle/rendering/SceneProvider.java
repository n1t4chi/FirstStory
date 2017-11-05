/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.scene.RenderedScene;

/**
 * @author: n1t4chi
 */
public interface SceneProvider {
    RenderedScene getNextScene();
}
