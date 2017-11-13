/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.controller;

/**
 * @author n1t4chi
 */
public class CameraEvent {

    private double posX;
    private double posY;
    private double posZ;
    private double rotationY;
    private double rotationX;

    CameraEvent( double posX, double posY, double posZ, double rotationY, double rotationX ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotationY = rotationY;
        this.rotationX = rotationX;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public double getRotationY() {
        return rotationY;
    }

    public double getRotationX() {
        return rotationX;
    }
}
