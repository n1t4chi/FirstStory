/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.AbsolutePostionable.Text;

import static com.firststory.firstoracle.IOUtilities.loadFontFromResource;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import sun.font.FontUtilities;

/**
 * Simple drawable static text box with ability to change text on the go.<br>
 * Best if used for long constant text displayed for longer periods of time.<br>
 * 
 * @author n1t4chi
 */
public class Text3DFactory implements AutoCloseable{
    
    
    @Override
    public void close(){
        if(workGraphics == null)
            workGraphics.dispose();
        workGraphics = null;
    }
    
    private final BufferedImage workImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    private Graphics2D workGraphics = null;
    private final Font font;
    private FontMetrics fm;
    
    /**
     * Default Constructor. Best if other contructors would just call this one as this one 
     * @param font 
     */
    public Text3DFactory(java.awt.Font font){
        this.font = font;
    }
    
    public static float getLineWidth(String s,FontMetrics fm){
        char[] chars = s.toCharArray();
        int width= 0;
        for(char c : chars){
            width+=fm.charWidth(c);
        }
        return width;
    }
    
    public Text3D getText3D(String text,int textPosX,int textPosY,int screenWidth, int screenHeight) throws IOException{
        if(workGraphics == null){
            workGraphics = workImage.createGraphics();
            workGraphics.setFont(font);
            fm = workGraphics.getFontMetrics();
        }
        String[] strings = text.split("\n");
        
        //get font data
        
        float fontAscent = fm.getAscent();
        float fontDescent = fm.getDescent();
        float fontHeight = fontAscent+fontDescent;
        float lineSpace = fm.getLeading();
        float lineHeight = fontHeight+lineSpace;
        
        float lineIndentation = fm.charWidth(' ');
        
        float firstAscent = fm.getMaxAscent()-fontAscent;
        float lastDescent = fm.getMaxDescent()-fontDescent;
        
        float imageWidth = 0;
        float imageHeight = firstAscent+ (fontHeight+lineSpace)*strings.length -lineSpace  +lastDescent;
        
        
        for(String s :strings){
            float width = getLineWidth(s, fm);
            if(width > imageWidth){
                imageWidth = width;
            }
        }
        imageWidth += 2*lineIndentation;
        
        
        
        //System.out.println("Image:"+imageWidth+"x"+imageHeight);        
        //System.out.println("Font: ascent"+fontAscent+", descent"+fontDescent+", height:"+fontHeight+", line space:"+lineSpace +", line height"+lineHeight);        
        //System.out.println("firstAscent:"+firstAscent+", lastDescent:"+lastDescent+", line indentation:"+lineIndentation);
        
        BufferedImage image = new BufferedImage((int)Math.ceil(imageWidth),(int)Math.ceil(imageHeight) , BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(Color.RED); 
        g.setStroke(new BasicStroke(5));
        g.drawRect(2, 2, image.getWidth()-2, image.getHeight()-2);
        
        g.setFont(font);
        g.setPaint(Color.WHITE); 
        
        float y = firstAscent-fontDescent;
        for(String s:strings){
            y+=fontHeight;
            g.drawString(s,lineIndentation,y);
            
        }
        g.dispose(); 
        return new Text3D(image,new Rectangle2D.Float(0,lastDescent+fontAscent,imageWidth ,imageHeight),textPosX,textPosY,screenWidth,screenHeight);
    }
    
    
    /**
     * Loads font using default java awt.Font creation.
     * @param fontName
     * @param style
     * @param size 
     * @throws IOException There is possibility of throwing IOException on Texture creation.
     */
    public Text3DFactory(String fontName,int style, int size) throws IOException {
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
    public Text3DFactory(String resourceName,int style, int size,boolean externalFile) throws IOException{
        this(loadFontFromResource(resourceName,externalFile).deriveFont(style, size));
    }

}
