/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

/**
 * @author: n1t4chi
 */
public class WindowSettings {

    private final WindowMode windowMode;
    private final String title;
    private final int antiAliasing;
    private final boolean verticalSync;
    private int width;
    private int height;
    private int positionX;
    private int positionY;
    private boolean drawGrid = false;
    private boolean drawBorder = false;
    private boolean useTexture = true;
    private boolean resizeable = false;

    public WindowSettings(
        WindowMode windowMode,
        String title,
        int width,
        int height,
        int positionX,
        int positionY,
        boolean verticalSync,
        boolean resizeable,
        int antiAliasing,
        boolean drawGrid,
        boolean drawBorder,
        boolean useTexture
    )
    {
        this.windowMode = windowMode;
        this.title = title;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.verticalSync = verticalSync;
        this.resizeable = resizeable;
        this.antiAliasing = antiAliasing;
        this.drawGrid = drawGrid;
        this.drawBorder = drawBorder;
        this.useTexture = useTexture;
    }

    public boolean isResizeable() {
        return resizeable;
    }

    public void setResizeable( boolean resizeable ) {
        this.resizeable = resizeable;
    }

    public WindowMode getWindowMode() {
        return windowMode;
    }

    public String getTitle() {
        return title;
    }

    public int getAntiAliasing() {
        return antiAliasing;
    }

    public boolean isVerticalSync() {
        return verticalSync;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth( int width ) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight( int height ) {
        this.height = height;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX( int positionX ) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY( int positionY ) {
        this.positionY = positionY;
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public void setDrawGrid( boolean drawGrid ) {
        this.drawGrid = drawGrid;
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public void setDrawBorder( boolean drawBorder ) {
        this.drawBorder = drawBorder;
    }

    public boolean isUseTexture() {
        return useTexture;
    }

    public void setUseTexture( boolean useTexture ) {
        this.useTexture = useTexture;
    }

    public static class WindowSettingsBuilder {

        private WindowMode windowMode = WindowMode.WINDOWED;
        private String title = "Window";
        private int antiAliasing = 8;
        private boolean verticalSync = false;
        private int width = 600;
        private int height = 300;
        private int positionX = 0;
        private int positionY = 0;
        private boolean drawGrid = false;
        private boolean drawBorder = false;
        private boolean useTexture = true;
        private boolean resizeable = false;

        public WindowSettings build() {
            return new WindowSettings(
                windowMode,
                title,
                width,
                height,
                positionX,
                positionY,
                verticalSync,
                resizeable,
                antiAliasing,
                drawGrid,
                drawBorder,
                useTexture
            );

        }

        public WindowSettingsBuilder setWindowMode( WindowMode windowMode ) {
            this.windowMode = windowMode;
            return this;
        }

        public WindowSettingsBuilder setTitle( String title ) {
            this.title = title;
            return this;
        }

        public WindowSettingsBuilder setAntiAliasing( int antiAliasing ) {
            this.antiAliasing = antiAliasing;
            return this;
        }

        public WindowSettingsBuilder setVerticalSync( boolean verticalSync ) {
            this.verticalSync = verticalSync;
            return this;
        }

        public WindowSettingsBuilder setResizeable( boolean resizeable ) {
            this.resizeable = resizeable;
            return this;
        }

        public WindowSettingsBuilder setWidth( int width ) {
            this.width = width;
            return this;
        }

        public WindowSettingsBuilder setHeight( int height ) {
            this.height = height;
            return this;
        }

        public WindowSettingsBuilder setPositionX( int positionX ) {
            this.positionX = positionX;
            return this;
        }

        public WindowSettingsBuilder setPositionY( int positionY ) {
            this.positionY = positionY;
            return this;
        }

        public WindowSettingsBuilder setDrawGrid( boolean drawGrid ) {
            this.drawGrid = drawGrid;
            return this;
        }

        public WindowSettingsBuilder setDrawBorder( boolean drawBorder ) {
            this.drawBorder = drawBorder;
            return this;
        }

        public WindowSettingsBuilder setUseTexture( boolean useTexture ) {
            this.useTexture = useTexture;
            return this;
        }
    }
}
