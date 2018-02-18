/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.JFXGL;

import cuchaz.jfxgl.JFXGL;

public class JfxglContext {
    private static JfxglContext instance = new JfxglContext();

    public static JfxglContext getInstance() {
        return instance;
    }
    
    private JfxglContext(){}
    
    public static void terminate() {
        JFXGL.terminate();
        instance = null;
    }
}
