/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.gui.GuiApplicationData;
import com.firststory.firstoracle.gui.GuiFramework;
import com.firststory.firstoracle.gui.GuiFrameworkProvider;
import com.firststory.firstoracle.notyfying.FpsListener;
import com.firststory.firstoracle.notyfying.QuitListener;
import com.firststory.firstoracle.notyfying.TimeListener;
import com.firststory.firstoracle.rendering.Renderer;
import com.firststory.firstoracle.rendering.RenderingFramework;
import com.firststory.firstoracle.rendering.RenderingFrameworkProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public class WindowImpl implements Window {
    
    private static final AtomicInteger instanceCounter = new AtomicInteger( 0 );
    private static final Logger logger = FirstOracleConstants.getLogger( Window.class );
    
    private final WindowSettings settings;
    private final ArrayList< TimeListener > timeListeners = new ArrayList<>( 3 );
    private final ArrayList< QuitListener > quitListeners = new ArrayList<>( 3 );
    private final ArrayList< FpsListener > fpsListeners = new ArrayList<>( 5 );
    private final GuiApplicationData< ? > guiApplicationData;
    private final Renderer renderer;
    private final WindowFrameworkProvider windowFrameworkProvider;
    private final RenderingFrameworkProvider renderingFrameworkProvider;
    private final GuiFrameworkProvider< ? > guiFrameworkProvider;
    private final RenderLoop renderLoop;
    private WindowContext context;
    private WindowFramework windowFramework;
    private RenderingFramework renderingFramework;
    private GuiFramework guiFramework;
    private int frameCount;
    private double lastFrameUpdate;
    private double lastFpsUpdate;
    private int lastFps;
    
    public WindowImpl(
        WindowSettings windowSettings,
        GuiApplicationData< ? > guiApplicationData,
        Renderer renderer,
        WindowFrameworkProvider windowFrameworkProvider,
        RenderingFrameworkProvider renderingFrameworkProvider,
        GuiFrameworkProvider< ? > guiFrameworkProvider,
        RenderLoop renderLoop
    ) {
        this.settings = windowSettings;
        this.guiApplicationData = guiApplicationData;
        this.renderer = renderer;
        this.windowFrameworkProvider = windowFrameworkProvider;
        this.renderingFrameworkProvider = renderingFrameworkProvider;
        this.guiFrameworkProvider = guiFrameworkProvider;
        this.renderLoop = renderLoop;
    }
    
    @Override
    public void init()  {
        try {
            windowFramework = windowFrameworkProvider.getWindowFramework();
            logger.finest( this + ": Window context: " + windowFramework );
            context = windowFramework.createWindowContext( settings, renderingFrameworkProvider.isOpenGL() );
            renderingFramework = renderingFrameworkProvider.getRenderingFramework( context );
            logger.finest( this + ": Rendering context: " + renderingFramework );
            renderingFramework.invoke( instance -> {
                setupCallbacks();
                instance.compileShaders();
                renderer.init();
            } );
        
            frameCount = 0;
            lastFps = 0;
            lastFrameUpdate = getTime();
            lastFpsUpdate = getLastFrameUpdate();
        } catch ( Exception ex ) {
            close(); //do not place in finally!
            throw new RuntimeException( ex );
        }
    }
    
    @Override
    public void setupRenderCycleVariables() {
        lastFrameUpdate = getTime();
        if ( frameCount % 100 == 0 ) {
            lastFps = ( int ) ( ( float ) frameCount / ( getLastFrameUpdate() - lastFpsUpdate ) );
            lastFpsUpdate = getLastFrameUpdate();
            frameCount = 0;
            notifyFpsListeners( lastFps );
        }
        frameCount++;
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public void setUpRunInsideRedneringFramework() {
        context.show();
        guiFramework = (( GuiFrameworkProvider< GuiApplicationData<?>> ) guiFrameworkProvider).provide( context, guiApplicationData );
        logger.finest( this + ": GUI context: " + guiFramework );
    }
    
    @Override
    public void setUpRun() {
        Thread.currentThread().setName( "Window" + instanceCounter.getAndIncrement() );
    }
    
    @Override
    public void tearDownRun() { }
    
    public void close() {
        if ( context != null ) {
            context.destroy();
            context = null;
        }
    }
    
    @Override
    public String toString() {
        return "FirstOracle Window@" + hashCode();
    }
    
    @Override
    public RenderLoop getRenderLoop() {
        return renderLoop;
    }
    
    @Override
    public Collection< FpsListener > getFpsListeners() {
        return fpsListeners;
    }
    
    @Override
    public Collection< TimeListener > getTimeListeners() {
        return timeListeners;
    }
    
    @Override
    public Collection< QuitListener > getQuitListeners() {
        return quitListeners;
    }
    
    @Override
    public WindowContext getContext() {
        return context;
    }
    
    @Override
    public WindowSettings getSettings() {
        return settings;
    }
    
    @Override
    public RenderingFramework getRenderingFramework() {
        return renderingFramework;
    }
    
    @Override
    public GuiFramework getGuiFramework() {
        return guiFramework;
    }
    
    @Override
    public WindowFramework getWindowFramework() {
        return windowFramework;
    }
    
    @Override
    public Renderer getRenderer() {
        return renderer;
    }
    
    @Override
    public double getLastFrameUpdate() {
        return lastFrameUpdate;
    }
    
    @Override
    public double getTime() {
        return windowFramework.getTime();
    }
}