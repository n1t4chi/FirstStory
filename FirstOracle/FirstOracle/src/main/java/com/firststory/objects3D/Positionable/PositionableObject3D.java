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
    private final Vector3f scale = new Vector3f(1,1,1);
    private final Vector3f rotation = new Vector3f(0,0,0);
    

    /**
     * Creates new object using given texture.
     * @param texture 
     */
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
    /**
     * Changes rotation of this object.<br>
     * Remember that scaling Y will result in base of the object being higher if lower points are not 0 on Y dimension.
     * @param scale new rotation
     */
    public void setScale(Vector3fc scale) {
        this.scale.set(scale);
    }
    /**
     * Changes scale of this object.
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public void setScale(float x,float y,float z) {
        this.scale.set(x,y,z);
    }
    /**
     * Changes X scale of this object.
     * @param x X coordinate
     */
    public void setScaleX(float x) {
        this.scale.set(x,this.scale.y,this.scale.z);
    }
    /**
     * Changes Y scale of this object.<br>
     * Remember that scaling Y will result in base of the object being higher if lower points are not 0 on Y dimension.
     * @param y Y coordinate
     */
    public void setScaleY(float y) {
        this.scale.set(this.scale.x,y,this.scale.z);
    }
    /**
     * Changes Z scale of this object.
     * @param z Z coordinate
     */
    public void setScaleZ(float z) {
        this.scale.set(this.scale.x,this.scale.y,z);
    }
    /**
     * Returns immutable scale vector of this object.
     * @return scale
     */
    @Override
    public Vector3fc getScale(){
        return scale.toImmutable();
    }
    /**
     * Returns mutable scale vector of this object. Use with caution!
     * @return mutable scale
     */
    public Vector3f getScaleMutable(){
        return scale;
    }
    
     /**
     * Changes rotation of this object.<br>
     * X,Y,Z coordinates of given vector indicate angles on each axis.<br>
     * Then position will be rotated relative to (1,0,0), (0,1,0) or (0,0,1) vector 
     * depending which axis rotation is currently calculated.
     * @param rotation new rotation angles with values in radians
     */
    public void setRotation(Vector3fc rotation) {
        this.rotation.set(rotation);
    }
    /**
     * Changes rotation of this object.
     * @param x angle on X coordinate in radians
     * @param y angle on Y coordinate in radians
     * @param z angle on Z coordinate in radians
     */
    public void setRotation(float x,float y,float z) {
        this.rotation.set(x,y,z);
    }
    /**
     * Changes X rotation of this object.
     * @param x angle on X coordinate in radians
     */
    public void setRotationX(float x) {
        setRotation(x,this.rotation.y,this.rotation.z);
    }
    /**
     * Changes Y rotation of this object.<br>
     * @param y angle on Y coordinate in radians
     */
    public void setRotationY(float y) {
        setRotation(this.rotation.x,y,this.rotation.z);
    }
    /**
     * Changes Z rotation of this object.
     * @param z angle on Z coordinate in radians
     */
    public void setRotationZ(float z) {
        setRotation(this.rotation.x,this.rotation.y,z);
    }
    /**
     * Returns immutable rotation angles of this object.
     * @return rotation
     */
    @Override
    public Vector3fc getRotation(){
        return rotation.toImmutable();
    }
    /**
     * Returns mutable rotation angles of this object. Use with caution!
     * @return mutable rotation
     */
    public Vector3f getRotationMutable(){
        return rotation;
    }
    
    
}
