/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

/**
 * @author: n1t4chi
 */
public class DummyRenderer3D implements Renderer3D {
    public static final Renderer3D DUMMY_RENDERER = new DummyRenderer3D();

    @Override
    public void init() {}

    @Override
    public void dispose() {}

    @Override
    public void render() {}
}
