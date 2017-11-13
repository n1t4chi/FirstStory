/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.controller;

/**
 * @author n1t4chi
 */
public interface CameraListener {

    void notify( CameraEvent event, CameraNotifier source );
}
