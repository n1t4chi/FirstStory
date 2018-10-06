/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.text;

import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.data.Scale2D;
import com.firststory.firstoracle.data.Scale3D;
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

    public Scale3D computeScale3D( int screenWidth, int screenHeight ) {
        var scaleX = 2 * ( float ) bounds.getWidth() / screenWidth;
        var scaleY = 2 * ( float ) bounds.getHeight() / screenHeight;
        return Scale3D.scale3( scaleX, scaleY, 1 );
    }
    
    public Scale2D computeScale2D( int screenWidth, int screenHeight ) {
        var scaleX = 2 * ( float ) bounds.getWidth() / screenWidth;
        var scaleY = 2 * ( float ) bounds.getHeight() / screenHeight;
        return Scale2D.scale2( scaleX, scaleY );
    }
    
    public Position2D computePosition2D( int textPosX, int textPosY, int screenWidth, int screenHeight ) {
        var dX = 2 * ( float ) textPosX / screenWidth - 1;
        var dY = 2 * ( float ) textPosY / screenHeight - 1;
        return Position2D.pos2( dX, dY );
    }
    
    public Position3D computePosition3D( int textPosX, int textPosY, int screenWidth, int screenHeight ) {
        var dX = 2 * ( float ) textPosX / screenWidth - 1;
        var dY = 2 * ( float ) textPosY / screenHeight - 1;
        return Position3D.pos3( dX, dY, 1 );
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
