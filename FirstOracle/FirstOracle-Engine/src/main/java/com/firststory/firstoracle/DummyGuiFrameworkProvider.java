/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle;

import com.firststory.firstoracle.window.GuiApplicationData;
import com.firststory.firstoracle.window.GuiFramework;
import com.firststory.firstoracle.window.GuiFrameworkProvider;
import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class DummyGuiFrameworkProvider implements GuiFrameworkProvider< GuiApplicationData< ? > > {
    
    public static DummyGuiFrameworkProvider getProvider() {
        return new DummyGuiFrameworkProvider();
    }
    
    @Override
    public GuiFramework provide( WindowContext window, GuiApplicationData< ? > guiApplicationData ) {
        return () -> {};
    }
    
    @Override
    public void terminate() {}
}
