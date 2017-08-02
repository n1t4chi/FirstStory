/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.AbsolutePositionable;

import com.firststory.objects3D.Object3D;
import com.firststory.objects3D.Object3DType;
import com.firststory.objects3D.Texture;
import java.io.IOException;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 *
 * @author n1t4chi
 */
public abstract class AbsPositionableObject3D extends Object3D{
    /**
     * Initialises this object with given texture.
     * @param texture 
     */
    public AbsPositionableObject3D(Texture texture) {
        super(texture);
    }    
    private int x;
    private int y;
    private int width;
    private int height;
    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
    public void setWidth(int width) {this.width = width;}
    public void setHeight(int height) {this.height = height;}
    public void setPosition(int x,int y){
        this.x = x;
        this.y = y;
    }
    public void setSize(int width,int height){
        this.width = width;
        this.height = height;
    }
    public void setBounds(int x,int y,int width,int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    
    
    public Vector3fc getPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public Vector3fc getScale() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    private static final Vector3fc ROTATION = new Vector3f(0,0,0);
    @Override
    public Vector3fc getRotation() {
        return ROTATION;
    }
    
}
