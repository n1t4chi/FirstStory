/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.sun.prism.es2.JFXGLContext;
import cuchaz.jfxgl.CalledByEventsThread;
import cuchaz.jfxgl.CalledByMainThread;
import cuchaz.jfxgl.controls.OpenGLPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

/**
 * @author: n1t4chi
 */
public final class WindowApplication extends Application {
    private OpenGLPane glpane;
    private Pane overlayPanel;
    private WindowOverlayInitialiser overlayInitialiser;

    public WindowApplication( WindowOverlayInitialiser overlayInitialiser ) {
        this.overlayInitialiser = overlayInitialiser;
    }

    public Pane getOverlayPanel() {
        return overlayPanel;
    }

    @Override
    @CalledByEventsThread
    public void start( Stage stage )
        throws IOException
    {
        // create the UI
        glpane = new OpenGLPane();
        glpane.setRenderer( ( context ) -> render( context ) );
        overlayPanel = new Pane();
        overlayInitialiser.init( overlayPanel );
        glpane.getChildren().add( overlayPanel );
        stage.setScene( new Scene( glpane ) );
    }

    @CalledByMainThread
    private void render( JFXGLContext context ) {

        GL11.glClearColor( 0.8f, 0.5f, 0.5f, 1f );
        GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
    }

    public interface WindowOverlayInitialiser {
        void init( Pane overlayPanel );
    }
}
