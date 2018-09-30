/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.FrameworkProviderContext;
import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.gui.GuiApplicationData;
import com.firststory.firstoracle.gui.GuiFrameworkProvider;
import com.firststory.firstoracle.rendering.*;

/**
 * @author n1t4chi
 */
public abstract class WindowBuilder< WindowType extends Window > {
    
    public static WindowBuilder< Window > simpleWindow( WindowSettings settings, Renderer renderer ) {
        return new SimpleWindowBuilder( settings, renderer );
    }
    
    public static WindowBuilder< Window > simpleWindow( WindowSettings settings, SceneProvider provider ) {
        return new SimpleWindowBuilder( settings, WindowRenderer.provide( provider ) );
    }
    
    public static WindowBuilder< RegistrableWindow > registrableWindow( WindowSettings settings ) {
        return new RegistrableWindowBuilder( settings, new RegistrableSceneProviderImpl() );
    }
    
    final WindowSettings settings;
    final Renderer renderer;
    WindowFrameworkProvider windowFrameworkProvider = FrameworkProviderContext.createWindowFrameworkProvider();
    RenderingFrameworkProvider renderingFrameworkProvider = FrameworkProviderContext.createRenderingFrameworkProvider();
    GuiFrameworkProvider< ? > guiFrameworkProvider = FrameworkProviderContext.createGuiFrameworkProvider();
    GuiApplicationData< ? > application = null;
    RenderLoop renderLoop = createRenderLoop();
    
    private RenderLoop createRenderLoop() {
        RenderLoop renderLoop = new RenderLoopImpl();
        if ( PropertiesUtil.isPropertyTrue( PropertiesUtil.RENDER_LOOP_PERFORMANCE_LOG_PROPERTY ) ) {
            renderLoop = new RenderLoopPerformanceTester( renderLoop );
        }
        if ( PropertiesUtil.isPropertyTrue( PropertiesUtil.FORCE_ONE_LOOP_CYCLE_PROPERTY ) ) {
            renderLoop = new RenderLoopEarlyExit( renderLoop );
        }
        return renderLoop;
    }
    
    private WindowBuilder( WindowSettings settings, Renderer renderer ) {
        this.settings = settings;
        this.renderer = renderer;
    }
    
    public WindowBuilder< WindowType > addWindowFrameworkProvider( WindowFrameworkProvider windowFrameworkProvider ) {
        this.windowFrameworkProvider = windowFrameworkProvider;
        return this;
    }
    
    public WindowBuilder< WindowType > addRenderingFrameworkProvider( RenderingFrameworkProvider renderingFrameworkProvider ) {
        this.renderingFrameworkProvider = renderingFrameworkProvider;
        return this;
    }
    
    public void setRenderLoop( RenderLoop renderLoop ) {
        this.renderLoop = renderLoop;
    }
    
    public < T extends GuiApplicationData< ? > > WindowBuilder< WindowType > addGuiFrameworkProvider(
        GuiFrameworkProvider< T > guiFrameworkProvider, T application
    ) {
        this.guiFrameworkProvider = guiFrameworkProvider;
        this.application = application;
        return this;
    }
    
    public abstract WindowType build();
    
    private static class SimpleWindowBuilder extends WindowBuilder< Window > {
    
        private SimpleWindowBuilder( WindowSettings settings, Renderer renderer ) {
            super( settings, renderer );
        }
        
        public Window build() {
            return new Window(
                settings,
                application,
                renderer,
                windowFrameworkProvider,
                renderingFrameworkProvider,
                guiFrameworkProvider,
                renderLoop
            );
        }
    }
    
    private static class RegistrableWindowBuilder extends WindowBuilder< RegistrableWindow > {
        
        private final RegistrableSceneProviderImpl provider;
        
        private RegistrableWindowBuilder( WindowSettings settings, RegistrableSceneProviderImpl provider ) {
            super( settings, WindowRenderer.provide( provider ) );
            this.provider = provider;
        }
    
        public RegistrableWindow build() {
            return new RegistrableWindow(
                settings,
                application,
                renderer,
                windowFrameworkProvider,
                renderingFrameworkProvider,
                guiFrameworkProvider,
                renderLoop,
                provider
            );
        }
    }
}
