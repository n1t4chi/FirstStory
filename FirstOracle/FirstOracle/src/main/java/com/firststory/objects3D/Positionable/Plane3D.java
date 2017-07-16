/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.Positionable;

import com.firststory.objects3D.Object3DType;
import static com.firststory.objects3D.PlaneUtil.TRIANGLES_PLANE_NO_VBO;
import com.firststory.objects3D.Texture;
import java.io.IOException;
import static com.firststory.objects3D.PlaneUtil.VERTEX_ID_PLANE;
import static com.firststory.objects3D.PlaneUtil.UV_ID_PLANE;
import static com.firststory.objects3D.PlaneUtil.getCommonPlaneVertex;
import static com.firststory.objects3D.PlaneUtil.getTrainglesPlane;
import static com.firststory.objects3D.PlaneUtil.getUVMapPlane;

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
    @Override
    public double getVertexID() {
        return VERTEX_ID_PLANE;
    }
    @Override
    public double getUVID() {
        return UV_ID_PLANE;
    }

    
    

    public Plane3D(String path, int frameCount, int lineCount) throws IOException {
        super(path, Object3DType.PLANE, frameCount, lineCount);
    }

    public Plane3D(Texture texture) {
        super(texture);
    }

    @Override
    public boolean usesVBOIndexing() {
        return true;
    }

    @Override
    public short[] getTrianglesVBO() {
        return getTrainglesPlane();
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
    public float[] getUV(int frameIter) {
        return getUVMapPlane(frameIter,getTexture().getFrameCount(),getTexture().getLineCount());
    }
    
    
}
