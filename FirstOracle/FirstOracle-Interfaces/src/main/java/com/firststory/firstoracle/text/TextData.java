/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.text;

import com.firststory.firstoracle.object.Texture;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author n1t4chi
 */
public class TextData {
    
    private final BufferedImage image;
    private final Texture texture;
    private final Rectangle2D bounds;
    
    public TextData( BufferedImage image, Rectangle2D textBounds) {
        texture = Texture.create( image );
        this.bounds = textBounds;
        this.image = image;
    }
    
    public Rectangle2D getBounds() {
        return bounds;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public Texture getTexture() {
        return texture;
    }
}
