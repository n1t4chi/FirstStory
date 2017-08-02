/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.AbsolutePostionable.Text;

import static com.firststory.firstoracle.IOUtilities.loadFontFromResource;
import com.firststory.objects3D.Object3D;
import com.firststory.objects3D.Object3DType;
import com.firststory.objects3D.PlaneUtil;
import static com.firststory.objects3D.AbsolutePostionable.Text.TextUtill.getFontMetrics;
import com.firststory.objects3D.Texture;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3fc;

/**
 * Not used and probably will be deleted unless something will change.
 * 
 * @author n1t4chi
 */
@Deprecated
public class FontASCII3D{
    
    private final BufferedImage workspaceImage = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
    private final Graphics2D workspaceGraphics = workspaceImage.createGraphics();
    private final FontMetrics metrics;
    
    
    
    private int imageHeight;
    private int imageWidth;
    private final HashMap<Character,Glyph> charBoundMap = new HashMap<>();
    private BufferedImage fontImage;
    private final Font font;
    private final Texture fontTexture;
    /**
     * Default constructor. All other constructors must call this.
     * @param font
     * @throws IOException There is possibility of throwing IOException on Texture creation.
     */
    public FontASCII3D(java.awt.Font font) throws IOException {
        this.font = font;
        metrics = getFontMetrics(font);
        imageHeight = metrics.getHeight();
        //int[] widths = metrics.getWidths();
        imageWidth = 0;
        char[] c = new char[1];
        
        Map<Character,Glyph> map = charBoundMap;
        for (c[0] = 32; c[0] < 256; c[0]++) {
            if (c[0] == 127) {
                continue;
            }
            Rectangle2D charBounds = metrics.getStringBounds(c,0,1, workspaceGraphics);
            double charHeight = charBounds.getHeight();
            double charWidth = charBounds.getWidth();
            Glyph gl = new Glyph((int) charBounds.getWidth(),charHeight,imageWidth,imageHeight);
            imageWidth+=charBounds.getWidth();
            map.put(c[0], gl);
        }
        workspaceGraphics.dispose();
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setPaint(Color.WHITE);
        for (c[0] = 32; c[0] < 256; c[0]++) {
            if (c[0] == 127) {
                continue;
            }
            Glyph gl = map.get(c[0]);
            g.drawChars(c, 0,1, (int)gl.x , imageHeight ); //- gl.height
        }
        g.dispose();
        fontImage = image;
        fontTexture = new Texture(image, 1,1,1,1);
    }
    /**
     * Loads font using default java awt.Font creation.
     * @param fontName
     * @param style
     * @param size 
     * @throws IOException There is possibility of throwing IOException on Texture creation.
     */
    public FontASCII3D(String fontName,int style, int size) throws IOException {
        this(new Font(fontName,style,size));
    }
    /**
     * Loads font form resource, either file within packed JAR or external file.
     * @param resourceName
     * @param externalFile true if resource is an external File, if false then it will be considered resource within jar file.
     * @param style
     * @param size
     * @throws IOException On any error relating to the reading of the given resource like file does not exist or has illegal content. Also there is possibility of throwing IOException on Texture creation.
     */
    public FontASCII3D(String resourceName,int style, int size,boolean externalFile) throws IOException{
        this(loadFontFromResource(resourceName,externalFile).deriveFont(style, size));
    }
    
    public TextObject3D getText(String text){
        int letterCount = text.length();
        final int triangles_per_letter = 4;
        final int space_coords_per_letter = 3;
        final int uv_coords_per_letter = 2;
        float[] triangles = new float[letterCount * triangles_per_letter * space_coords_per_letter];
        float[] uv = new float[letterCount * triangles_per_letter * uv_coords_per_letter];
        
        
        Rectangle2D bnds = metrics.getStringBounds(text, workspaceGraphics);
        TextObject3D rtrn = new TextObject3D(fontTexture,triangles, uv,bnds);
        
        return rtrn;
    }
    
    public class TextObject3D extends Object3D{
        final private float[] triangles;
        final private float[] uv;
        final private Rectangle2D bounds;

        public TextObject3D(Texture texture,float[] triangles, float[] uv, Rectangle2D bounds) {
            super(texture);
            this.triangles = triangles;
            this.uv = uv;
            this.bounds = bounds;
        }

        @Override
        public float[] getTriangles() {
            return triangles;
        }

        @Override
        public float[] getVertex() {
            throw new UnsupportedOperationException("No VBO support."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Object3DType getType() {
            return Object3DType.GLYPH_TEXT;
        }
        /**
         * Returns UV map
         * @param frame not used
         * @param direction not used
         * @return 
         */
        @Override
        public float[] getUV(int frame,int direction) {
            return uv;
        }

        @Override
        public Vector3fc getScale() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Vector3fc getRotation() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isVertexBufferLoaded() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getVertexBufferID() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setVertexBufferID(int VertexBufferID) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }


        @Override
        public boolean isUVBufferLoaded(int frame, int direction) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setUVBufferID(int UVBufferID, int frame, int direction) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getUVBufferID(int frame, int direction) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
    

}
