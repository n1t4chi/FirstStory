/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.javafx.jfxgl;

import com.firststory.firstoracle.Runner;
import com.firststory.firstoracle.window.GuiFramework;
import com.firststory.firstoracle.window.GuiFrameworkProvider;
import com.firststory.firstoracle.window.WindowContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author n1t4chi
 */
public class JavaFxFrameworkProvider implements GuiFrameworkProvider< JavaFxApplication > {
    private static Set< JfxglContext > contexts = new HashSet<>(  );
    public static JavaFxFrameworkProvider getProvider() {
        JavaFxFrameworkProvider instance = new JavaFxFrameworkProvider();
        Runner.registerFramework( instance );
        return instance;
    }
    
    @Override
    public GuiFramework provide( WindowContext window, JavaFxApplication data ) {
        JfxglContext instance = JfxglContext.createInstance( window.getAddress(), new String[]{}, data.getData() );
        contexts.add( instance );
        return instance;
    }
    @Override
    public void terminate() {
        contexts.forEach( JfxglContext::terminate );
    }
}
