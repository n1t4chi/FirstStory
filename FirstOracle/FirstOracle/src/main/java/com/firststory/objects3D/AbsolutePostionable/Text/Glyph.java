/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.AbsolutePostionable.Text;

/**
 * Not used and probably will be deleted unless something will change.
 * @author n1t4chi
 */
@Deprecated
public class Glyph {
    public final double width;
    public final double height;
    public final double x;
    public final double y;

    public Glyph(double width, double height, double x, double y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }   

    @Override
    public String toString() {
        return "x:"+x+" y:"+y+" wid:"+width+" hei:"+height;
    }
    
    

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    
    
}
