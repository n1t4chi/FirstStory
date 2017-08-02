/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.AbsolutePostionable.Text;

import com.firststory.objects3D.Object3DType;
import com.firststory.objects3D.PlaneUtil;
import static com.firststory.objects3D.PlaneUtil.*;
import com.firststory.objects3D.Positionable.PositionableObject3D;
import com.firststory.objects3D.Texture;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author n1t4chi
 */
public class Text3D extends PositionableObject3D{
    BufferedImage image;
    Rectangle2D bounds;

    public Rectangle2D getBounds() {
        return bounds;
    }

    public BufferedImage getImage() {
        return image;
    }
    
    
    /**
     * Creates 
     * @param image
     * @param textBounds
     * @param textPosX
     * @param textPosY
     * @param screenWidth
     * @param screenHeight
     * @throws IOException 
     */
    public Text3D(BufferedImage image, Rectangle2D textBounds,int textPosX,int textPosY,int screenWidth,int screenHeight) throws IOException {
        super(new Texture(image));
        //super(new Texture(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)));
        
        this.bounds = textBounds;
        this.image = image;
        //System.out.println();
        //System.out.println("Text bounds:"+textBounds);
        //System.out.println("Image size:"+image.getWidth()+","+image.getHeight());
        //System.out.println("Text position:"+textPosX+","+textPosY);
        //System.out.println("Screen size:"+screenWidth+","+screenHeight);
        //System.out.println();
        
        float scaleX = 2*(float)textBounds.getWidth()/screenWidth;
        float scaleY = 2*(float)textBounds.getHeight()/screenHeight;
        
        float dX = 2*(float)textPosX / screenWidth -1;
        float dY = 2*(float)textPosY / screenHeight -1;
        
        //System.out.println("TextPos on screen:"+textPosX+","+textPosY+"  Scale:"+scaleX+","+scaleY+" Pos:"+dX+","+dY);
        
        setScale(scaleX,scaleY,1);
        setPosition(dX,dY,0);
    }

    @Override
    public float[] getTriangles() {
        return PlaneUtil.TRIANGLES_PLANE_NO_VBO_0INDEXED;
    }

    @Override
    public float[] getVertex() {
        return PlaneUtil.VERTEX_PLANE_0INDEXED;
    }

    @Override
    public Object3DType getType() {
        return Object3DType.STATIC_TEXT;
    }
    /**
     * Returns UV map
     * @param frame not used
     * @param direction not used
     * @return 
     */
    @Override
    public float[] getUV(int frame,int direction) {
        return PlaneUtil.getUVMapPlane(0,0,1,1,1,1);
    }

    
    @Override
    public boolean isVertexBufferLoaded() {
        return getPlane_0IndexedVertexBufferID() > 0;
    }

    @Override
    public int getVertexBufferID() {
        return getPlane_0IndexedVertexBufferID();
    }

    @Override
    public void setVertexBufferID(int VertexBufferID) {
        setPlane_0IndexedVertexBufferID(VertexBufferID);
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
