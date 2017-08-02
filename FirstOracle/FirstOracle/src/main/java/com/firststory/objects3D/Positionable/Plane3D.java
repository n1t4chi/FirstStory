/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.Positionable;

import com.firststory.objects3D.Object3DType;
import com.firststory.objects3D.Texture;
import static com.firststory.objects3D.PlaneUtil.*;

/**
 * Class representing 3D plane.<br>
 * Model for plane is:<br>
 *   /\y<br>
 *   |<br>
 *   25---24<br>
 *   |     |<br>
 *   |     |<br>
 *   22---23-&gt;x<br>
 *  /<br>
 * /<br>
 *|/z <br>
 * Centre of the plane is in (0,0,0) and X and Y coordinates are within [-1,1] range while Z is fixed at 0.<br>
 * <br>
 * There is only single face which points towards user which uses whole line of texture.
 * 
 * 
 * @author n1t4chi
 */
public class Plane3D extends PositionableObject3D{
    
    

    /**
     * Initialises this object with given texture.
     * @param texture 
     */
    public Plane3D(Texture texture) {
        super(texture);
    }    

    @Override
    public float[] getTriangles() {
        //throw new UnsupportedOperationException("Plane uses VBO for triangle representation");
        return TRIANGLES_PLANE_NO_VBO;
    }

    @Override
    public float[] getVertex() {
        return getCommonPlaneVertex();
    }

    @Override
    public Object3DType getType() {
        return Object3DType.PLANE;
    }
     
    @Override
    public float[] getUV(int frame,int direction) {
        Texture tex = getTexture();
        return getUVMapPlane(frame,direction,tex.getFrames(),tex.getDirections(),tex.getRows(),tex.getColumns());
    }
    
    
    
    @Override
    public boolean isVertexBufferLoaded() {
        return getPlaneVertexBufferID() > 0;
    }

    @Override
    public int getVertexBufferID() {
        return getPlaneVertexBufferID();
    }

    @Override
    public void setVertexBufferID(int VertexBufferID) {
        setPlaneVertexBufferID(VertexBufferID);
    }


    @Override
    public boolean isUVBufferLoaded(int frame, int direction) {
        return getPlaneUVBufferID(frame, direction, getRows(),getColumns()) >0;
    }

    @Override
    public void setUVBufferID(int UVBufferID, int frame, int direction) {
        setPlaneUVBufferID(UVBufferID, frame, direction, getRows(), getColumns());
    }

    @Override
    public int getUVBufferID(int frame, int direction) {
        return getPlaneUVBufferID(frame, direction, getRows(), getColumns());
    }
    
    
    
}
