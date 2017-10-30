/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle;

import java.io.IOException;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import cuchaz.jfxgl.CalledByEventsThread;
import cuchaz.jfxgl.CalledByMainThread;
import cuchaz.jfxgl.JFXGL;
import cuchaz.jfxgl.JFXGLLauncher;
import cuchaz.jfxgl.controls.OpenGLPane;
import com.sun.prism.es2.JFXGLContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelloWorldPane {

    public static void main(String[] args) {
        JFXGLLauncher.launchMain(HelloWorldPane.class, args);
    }
    
    public static void jfxglmain(String[] args)
        throws Exception {
        GLFW.glfwInit();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        long hwnd = GLFW.glfwCreateWindow(300, 169, "JFXGL", MemoryUtil.NULL, MemoryUtil.NULL);

        // init OpenGL
        GLFW.glfwMakeContextCurrent(hwnd);
        GL.createCapabilities();

        try {

            // start the JavaFX app
            JFXGL.start(hwnd, args, new HelloWorldPaneApp());

            // render loop
            while (!GLFW.glfwWindowShouldClose(hwnd)) {

                // render the JavaFX UI
                JFXGL.render();

                GLFW.glfwSwapBuffers(hwnd);
                GLFW.glfwPollEvents();
            }

        } finally {

            // cleanup
            JFXGL.terminate();
            Callbacks.glfwFreeCallbacks(hwnd);
            GLFW.glfwDestroyWindow(hwnd);
            GLFW.glfwTerminate();
        }
    }

    public static class HelloWorldPaneApp extends Application {

        private OpenGLPane glpane;

        @Override
        @CalledByEventsThread
        public void start(Stage stage)
            throws IOException {

            // create the UI
            glpane = new OpenGLPane();
            glpane.setRenderer((context) -> render(context));
            glpane.getChildren().add(new Label("Hello World!"));
            stage.setScene(new Scene(glpane));
        }

        @CalledByMainThread
        private void render(JFXGLContext context) {

            GL11.glClearColor(0.8f, 0.5f, 0.5f, 1f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        }
    }
}