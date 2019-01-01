/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.text;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static com.firststory.firstoracle.templates.IOUtilities.loadFontFromResource;

/**
 * Simple drawable static text box with ability to change text on the go.<br>
 * Best if used for long constant text displayed for longer periods of time.<br>
 *
 * @author n1t4chi
 */
public class TextImageFactory {
    
    private static final TextImageFactory defaultInstance = provide( new Font( "Times New Roman", Font.PLAIN, 20 ) );
    
    public static TextImageFactory provide() {
        return defaultInstance;
    }
    
    public static TextImageFactory provide( int size ) {
        return provide( new Font( "Times New Roman", Font.PLAIN, size ) );
    }
    
    public static TextImageFactory provide( String fontName, int style, int size ) {
        return provide( new Font( fontName, style, size ) );
    }
    
    public static TextImageFactory provide( String resourceName, int style, int size, boolean externalFile ) {
        return provide( loadFontFromResource( resourceName, externalFile ).deriveFont( style, size ) );
    }
    
    public static TextImageFactory provide( java.awt.Font font ) {
        return new TextImageFactory( font );
    }
    
    private final BufferedImage workImage = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
    private final Font font;
    
    private Graphics2D workGraphics;
    private FontMetrics fm;
    
    public TextImageFactory( java.awt.Font font ) {
        this.font = font;
    }
    
    public void dispose() {
        if ( workGraphics != null ) {
            workGraphics.dispose();
            workGraphics = null;
        }
    }
    
    public TextData createText3D( String text ) {
        if ( workGraphics == null ) {
            workGraphics = workImage.createGraphics();
            workGraphics.setFont( font );
            fm = workGraphics.getFontMetrics();
        }
        
        var strings = text.split( "\n" );
        //get font data
        
        var fontAscent = fm.getAscent();
        var fontDescent = fm.getDescent();
        var fontHeight = fontAscent + fontDescent;
        var lineSpace = fm.getLeading();
        var lineHeight = fontHeight + lineSpace;
        
        var lineIndentation = fm.charWidth( ' ' );
        
        var firstAscent = fm.getMaxAscent() - fontAscent;
        var lastDescent = fm.getMaxDescent() - fontDescent;
        
        var imageWidth = 0f;
        var imageHeight = firstAscent + ( fontHeight + lineSpace ) * strings.length - lineSpace + lastDescent;
        
        for ( var s : strings ) {
            var width = getLineWidth( s, fm );
            if ( width > imageWidth ) {
                imageWidth = width;
            }
        }
        imageWidth += 2 * lineIndentation;
        
        var image = new BufferedImage( ( int ) Math.ceil( imageWidth ), ( int ) Math.ceil( imageHeight ), BufferedImage.TYPE_INT_ARGB );
        
        var g = image.createGraphics();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setPaint( Color.RED );
        g.setStroke( new BasicStroke( 5 ) );
//        g.drawRect( 2, 2, image.getWidth() - 2, image.getHeight() - 2 );
        
        g.setFont( font );
        g.setPaint( Color.WHITE );
        
        var y = firstAscent - fontDescent;
        for ( var s : strings ) {
            y += fontHeight;
            g.drawString( s, lineIndentation, y );
            
        }
        g.dispose();
        var textBounds = new Rectangle2D.Float( 0, lastDescent + fontAscent, imageWidth, imageHeight );
        
        return new TextData( image, textBounds );
    }
    
    private float getLineWidth( String s, FontMetrics fm ) {
        var chars = s.toCharArray();
        var width = 0;
        for ( var c : chars ) {
            width += fm.charWidth( c );
        }
        return width;
    }
    
}
