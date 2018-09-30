/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.gui.GuiApplicationData;
import com.firststory.firstoracle.gui.GuiFramework;
import com.firststory.firstoracle.gui.GuiFrameworkProvider;

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
