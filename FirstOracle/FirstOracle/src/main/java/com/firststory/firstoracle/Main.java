/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle;



/**
 * Main class that initialises whole test application.
 * @author n1t4chi
 */
public class Main {
public static class vec3{
    public float x,y,z;

    public vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public String toString() {
        return "x:"+x+" y:"+y+" z:"+z;
    }
    }
static float sin(float a){
    return (float) Math.sin(a);
} 
static float cos(float a){
    return (float) Math.cos(a);
}

public static
void rotateX(vec3 pos,float angleX){
    float y = pos.y;
    float z = pos.z;
    
    float sinX = sin(angleX);
    float cosX = cos(angleX);
    
}
public static
void rotateY(vec3 pos,float angleY){
    float x = pos.x;
    float z = pos.z;
    
    float sinY = sin(angleY);
    float cosY = cos(angleY);
    
    pos.x = x*cosY - z*sinY;
    pos.z = x*sinY + z*cosY;
}
public static
void rotateZ(vec3 pos,float angleZ){
    float x = pos.x;
    float y = pos.y;
    
    float sinZ = sin(angleZ);
    float cosZ = cos(angleZ);
    
    pos.x = x*cosZ - y*sinZ;
    pos.y = x*sinZ + y*cosZ;
}
public static
void rotate(vec3 pos,vec3 rot){
    
    float sinX = sin(rot.x);
    float cosX = cos(rot.x);
    float sinY = sin(rot.y);
    float cosY = cos(rot.y);
    float sinZ = sin(rot.z);
    float cosZ = cos(rot.z);
    
    pos.y = pos.y*cosX - pos.z*sinX;
    pos.z = pos.y*sinX + pos.z*cosX;
    
    pos.x = pos.x*cosY - pos.z*sinY;
    pos.z = pos.x*sinY + pos.z*cosY;
    
    pos.x = pos.x*cosZ - pos.y*sinZ;
    pos.y = pos.x*sinZ + pos.y*cosZ;
}
    
    
    public static void main(String[] args) {
       /* vec3 v1 = new vec3(0.5f,0,0.5f);
        vec3 v2 = new vec3(-0.5f,0,0.5f);
        vec3 v3 = new vec3(-0.5f,0,-0.5f);
        vec3 v4 = new vec3(0.5f,0,-0.5f);
        rotateZ(v1, (float) Math.toRadians(45) );
        rotateZ(v2, (float) Math.toRadians(45) );
        rotateZ(v3, (float) Math.toRadians(45) );
        rotateZ(v4, (float) Math.toRadians(45) );
        System.out.println(v1);
        System.out.println(v2);
        System.out.println(v3);
        System.out.println(v4);*/
        
        
        Window win = new Window(Window.WINDOW_MODE.WINDOWED, "gaem", 800, 600,0,0,false, 16);
        //win.run(Window.WINDOW_MODE.BORDERLESS, "gaem", -1, -1,-1,-1, 4);
        win.run();
       
       
       /* try {
            Text3DFactory tf = new Text3DFactory("Times New Roman",Font.PLAIN,42);
            Text3D t3 = tf.getText3D("I don't associate with niggers!");
            JFrame w = new JFrame();
            w.setSize(1000,500);
            w.setLayout(new BorderLayout());
            JLabel l = new JLabel(new ImageIcon(t3.getImage()));
            w.add(l,BorderLayout.CENTER);
            w.getContentPane().setBackground(Color.BLACK);
            l.setText("wtf");
            w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            w.setVisible(true);
        } catch (IOException ex) {
            System.err.println("Error:"+ex);
        }*/
    }
}
