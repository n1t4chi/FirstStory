/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.camera.camera2D.Camera2D;
import com.firststory.firstoracle.camera.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.camera.camera3D.Camera3D;
import com.firststory.firstoracle.camera.camera3D.IsometricCamera3D;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * @author: n1t4chi
 */
public class RenderedScene {
    private Camera2D camera2D;
    private IsometricCamera3D isometricCamera3D;
    private Vector4fc backgroundColour;

    public IsometricCamera3D getIsometricCamera3D() {
        return isometricCamera3D;
    }
    public Camera2D getCamera2D() {
        return camera2D;
    }

    public RenderedScene(){
        camera2D = new IdentityCamera2D();
        isometricCamera3D = new IsometricCamera3D( 10,0,0,0,1,0,0,1 );
        backgroundColour = new Vector4f( 1,1,1,1 );
    }


    public RenderedScene(
        Camera2D camera2D, IsometricCamera3D isometricCamera3D, Vector4fc backgroundColour
    )
    {
        this.camera2D = camera2D;
        this.isometricCamera3D = isometricCamera3D;
        this.backgroundColour = backgroundColour;
    }

    public void setIsometricCamera3D( IsometricCamera3D isometricCamera3D ) {
        this.isometricCamera3D = isometricCamera3D;
    }

    public Vector4fc getBackgroundColour() {
        return backgroundColour;
    }

    public void setBackgroundColour( Vector4fc backgroundColour ) {
        this.backgroundColour = backgroundColour;
    }

    public void setCamera2D( Camera2D camera2D ) {
        this.camera2D = camera2D;
    }
}
