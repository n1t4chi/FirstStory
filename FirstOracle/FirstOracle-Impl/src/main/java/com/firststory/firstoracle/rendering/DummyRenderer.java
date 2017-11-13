/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

/**
 * @author n1t4chi
 */
public class DummyRenderer implements GraphicRenderer {
    public static final GraphicRenderer DUMMY_RENDERER = new DummyRenderer();

    @Override
    public void init() {}

    @Override
    public void dispose() {}

    @Override
    public void render() {}
}
