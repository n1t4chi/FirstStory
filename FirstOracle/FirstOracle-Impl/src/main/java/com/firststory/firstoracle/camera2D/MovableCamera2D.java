/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera2D;

import com.firststory.firstoracle.WindowSettings;
import org.joml.*;

/**
 * @author n1t4chi
 */
public class MovableCamera2D extends Camera2D {

    private final Vector2f position;
    private final Matrix3f camera;
    private final Matrix3f inverseCamera;
    private float width;
    private float rotation;
    private WindowSettings settings;

    public MovableCamera2D(
            WindowSettings settings, float width, float X, float Y, float rotation
    ) {
        this.settings = settings;
        this.position = new Vector2f(X, Y);
        this.rotation = rotation;
        camera = new Matrix3f();
        inverseCamera = new Matrix3f();
        this.width = width;
    }

    public void setRotation(float rotation) {
        if (this.rotation != rotation) {
            this.rotation = rotation;
            forceUpdate();
        }
    }

    public void setWidth(float width) {
        if (this.width != width) {
            this.width = width;
            forceUpdate();
        }
    }

    public final void setTranslation(float X, float Y) {
        if (position.x != X || position.y != Y) {
            this.position.set(X, Y);
            forceUpdate();
        }
    }

    @Override
    public Matrix3fc getMatrixRepresentation() {
        updateMatrix();
        return camera;
    }

    @Override
    public Vector2fc getCenterPoint() {
        Vector3f vector = new Vector3f(0, 0, 1);
        inverseCamera.transform(vector);
        return new Vector2f(vector.x, vector.y);
    }

    @Override
    public boolean contains(float minX, float maxX, float minY, float maxY) {
        return true;
    }

    @Override
    public Vector2fc translatePointOnScreen(float x, float y, int width, int height) {
        if (camera.determinant() != 0) {
            float cameraX = 2f * x / width - 1f;
            float cameraY = -(2f * y / height - 1f);
            Vector3f vector = new Vector3f(cameraX, cameraY, 1);
            inverseCamera.transform(vector);
            return new Vector2f(vector.x, vector.y);
        } else {
            throw new RuntimeException("Cannot invert camera:\n" + "width:" + width + " ratio:" +
                    settings.getHeightByWidthRatio() + " rotation:" + rotation +
                    " position:(" + position.x + "," + position.y + ")");
            //return new Vector2f( 0,0 );
        }
    }

    private void updateMatrix() {
        if (mustUpdate()) {
            camera.identity();
            camera.scale(1 / width, 1 / (width * settings.getHeightByWidthRatio()), 1);
            camera.rotateZ((float) java.lang.Math.toRadians(rotation));
            translate();
            if (camera.determinant() != 0) {
                camera.invert(inverseCamera);
            } else {
                System.err.println("Cannot invert camera:\n" + "width:" + width + " ratio:" +
                        settings.getHeightByWidthRatio() + " rotation:" + rotation +
                        " position:(" + position.x + "," + position.y + ")");
            }
            updated();
        }
    }

    private void translate() {
        camera.m20 = position.x;
        camera.m21 = position.y;
    }
}
