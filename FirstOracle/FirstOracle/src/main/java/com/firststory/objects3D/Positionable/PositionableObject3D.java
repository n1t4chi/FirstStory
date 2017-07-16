/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.Positionable;

import com.firststory.objects3D.Object3D;
import com.firststory.objects3D.Object3DType;
import com.firststory.objects3D.Texture;
import java.io.IOException;
import org.joml.Vector3f;
import org.joml.Vector3fc;


/**
 * Class representing 3D model, also contains some common data declarations and static utility methods.<br>
 * There are 2 methods {@link #getTrianglesVBO()} and {@link #getTriangles()}
 * which should be used depending whether the {@link #usesVBOIndexing()} returns true or false respectively.<br>
 * Classes that extend this interface should throw exception if other method is used since data type is vastly different and also the usage.
 * @author n1t4chi
 */
public abstract class PositionableObject3D extends Object3D {
    private final Vector3f position = new Vector3f(0,0,0);
    

    public PositionableObject3D(String path,Object3DType type,int frameCount, int lineCount ) throws IOException {
        super(path, type, frameCount, lineCount);
    }
    public PositionableObject3D(Texture texture) {
        super(texture);
    }
    
    
    /**
     * Changes position of this object
     * @param position new position
     */
    public void setPosition(Vector3fc position) {
        this.position.set(position);
    }
    /**
     * Changes position of this object
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public void setPosition(float x,float y,float z) {
        this.position.set(x,y,z);
    }
    /**
     * Changes X position of this object
     * @param x X coordinate
     */
    public void setPositionX(float x) {
        this.position.set(x,this.position.y,this.position.z);
    }
    /**
     * Changes Y position of this object
     * @param y Y coordinate
     */
    public void setPositionY(float y) {
        this.position.set(this.position.x,y,this.position.z);
    }
    /**
     * Changes Z position of this object
     * @param z Z coordinate
     */
    public void setPositionZ(float z) {
        this.position.set(this.position.x,this.position.y,z);
    }
    /**
     * Returns immutable position vector of this object (Position of centre point (0,0,0) in object model).
     * @return position
     */
    public Vector3fc getPosition(){
        return position.toImmutable();
    }
    /**
     * Returns mutable position vector of this object (Position of centre point (0,0,0) in object model). Use with caution!
     * @return mutable position
     */
    public Vector3f getPositionMutable(){
        return position;
    }
}
