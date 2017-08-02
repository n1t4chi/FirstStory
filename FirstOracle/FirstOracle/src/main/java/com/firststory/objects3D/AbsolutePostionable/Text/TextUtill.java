/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.AbsolutePostionable.Text;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;

/**
 *
 * @author n1t4chi
 */
public class TextUtill {
    
    public static FontMetrics getFontMetrics(java.awt.Font font){
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        g.dispose();
        return metrics;
    }
    public static LineMetrics getLineMetrics(java.awt.Font font,String text){
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(text, g);
        g.dispose();
        return lm;
    }
    
    
}
