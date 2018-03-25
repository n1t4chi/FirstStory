/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.window.notifying.FpsListener;
import com.firststory.firstoracle.window.notifying.FpsNotifier;
import com.firststory.firstoracle.window.notifying.TimeListener;
import com.firststory.firstoracle.window.notifying.TimeNotifier;
import com.sun.prism.es2.JFXGLContext;
import cuchaz.jfxgl.CalledByEventsThread;
import cuchaz.jfxgl.CalledByMainThread;
import cuchaz.jfxgl.JFXGL;
import cuchaz.jfxgl.controls.OpenGLPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author n1t4chi
 */
public final class WindowApplication extends Application implements FpsListener, TimeListener {
    
    private final OverlayContentManager contentUpdater;
    private int lastFpsUpdate = 0;
    private double lastTimeUpdate;
    
    public WindowApplication( OverlayContentManager contentUpdater ) {
        this.contentUpdater = contentUpdater;
    }
    
    @Override
    @CalledByEventsThread
    public void start( Stage stage ) throws IOException {
        // create the UI
        OpenGLPane glpane = new OpenGLPane();
        Pane overlayPanel = contentUpdater.createOverlayPanel();
        
        glpane.setRenderer( this::render );
        glpane.getChildren().add( overlayPanel );
        
        Scene scene = new Scene( glpane );
        stage.setScene( scene );
        contentUpdater.init( stage, scene );
    }
    
    @Override
    public void notify( int newFpsCount, FpsNotifier source ) {
        lastFpsUpdate = newFpsCount;
    }
    
    @Override
    public void notify( double newTimeSnapshot, TimeNotifier source ) {
        lastTimeUpdate = newTimeSnapshot;
    }
    
    @CalledByMainThread
    private void render( JFXGLContext context ) {
        JFXGL.runOnEventsThread( () -> contentUpdater.update( lastTimeUpdate, lastFpsUpdate ) );
    }
}
