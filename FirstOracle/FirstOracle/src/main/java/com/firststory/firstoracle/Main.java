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

    public static void main(String[] args) {
        Window win = new Window();
        win.run(Window.WINDOW_MODE.BORDERLESS, "gaem", -1, -1,-1,-1, 4);
        //win.run(Window.WINDOW_MODE.WINDOWED, "gaem", 500, 300,0,0, 4);
    }
}
