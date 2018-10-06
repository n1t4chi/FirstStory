/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.FrameworkProviderContext;
import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.gui.GuiApplicationData;
import com.firststory.firstoracle.gui.GuiFrameworkProvider;
import com.firststory.firstoracle.rendering.Renderer;
import com.firststory.firstoracle.rendering.RenderingFrameworkProvider;
import com.firststory.firstoracle.rendering.WindowRenderer;
import com.firststory.firstoracle.scene.RegistrableScene;
import com.firststory.firstoracle.scene.RegistrableSceneProvider;
import com.firststory.firstoracle.scene.RegistrableSceneProviderImpl;
import com.firststory.firstoracle.scene.SceneProvider;

/**
 * @author n1t4chi
 */
public abstract class WindowBuilder< WindowType extends WindowImpl > {
    
    public static WindowBuilder< WindowImpl > simpleWindow( WindowSettings settings, Renderer renderer ) {
        return new SimpleWindowBuilder( settings, renderer );
    }
    
    public static WindowBuilder< WindowImpl > simpleWindow( WindowSettings settings, SceneProvider provider ) {
        return new SimpleWindowBuilder( settings, WindowRenderer.provide( provider ) );
    }
    
    public static RegistrableWindowBuilder registrableWindow( WindowSettings settings ) {
        return new RegistrableWindowBuilder( settings, new RegistrableSceneProviderImpl<>() );
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
    
    public WindowBuilder< WindowType > setRenderLoop( RenderLoop renderLoop ) {
        this.renderLoop = renderLoop;
        return this;
    }
    
    public < T extends GuiApplicationData< ? > > WindowBuilder< WindowType > addGuiFrameworkProvider(
        GuiFrameworkProvider< T > guiFrameworkProvider,
        T application
    ) {
        this.guiFrameworkProvider = guiFrameworkProvider;
        this.application = application;
        return this;
    }
    
    public WindowType build() {
        WindowType window = createWindowInstance();
        window.init();
        return window;
    }
    
    protected abstract WindowType createWindowInstance();
    
    public static class SimpleWindowBuilder extends WindowBuilder< WindowImpl > {
    
        private SimpleWindowBuilder( WindowSettings settings, Renderer renderer ) {
            super( settings, renderer );
        }
    
        protected WindowImpl createWindowInstance() {
            return new WindowImpl(
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
    
    public static class RegistrableWindowBuilder extends WindowBuilder< RegistrableWindow > {
        
        private final RegistrableSceneProvider< RegistrableScene > provider;
    
        private RegistrableWindowBuilder( WindowSettings settings, RegistrableSceneProvider< RegistrableScene > provider ) {
            super( settings, WindowRenderer.provide( provider ) );
            this.provider = provider;
        }
    
        protected RegistrableWindow createWindowInstance() {
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
    
        @Override
        public RegistrableWindowBuilder addWindowFrameworkProvider( WindowFrameworkProvider windowFrameworkProvider ) {
            super.addWindowFrameworkProvider( windowFrameworkProvider );
            return this;
        }
    
        @Override
        public RegistrableWindowBuilder addRenderingFrameworkProvider( RenderingFrameworkProvider renderingFrameworkProvider ) {
            super.addRenderingFrameworkProvider( renderingFrameworkProvider );
            return this;
        }
    
        @Override
        public RegistrableWindowBuilder setRenderLoop( RenderLoop renderLoop ) {
            super.setRenderLoop( renderLoop );
            return this;
        }
    
        @Override
        public < T extends GuiApplicationData< ? > > RegistrableWindowBuilder addGuiFrameworkProvider(
            GuiFrameworkProvider< T > guiFrameworkProvider, T application
        ) {
            super.addGuiFrameworkProvider( guiFrameworkProvider, application );
            return this;
        }
    }
}
