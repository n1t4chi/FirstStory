/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Index3D;

/**
 * @author n1t4chi
 */
public class RegistrableSceneImpl implements RegistrableScene {
    
    public static RegistrableSceneImpl provide3D( Index3D terrain3DSize, Index3D terrain3DShift ) {
        return provide( FirstOracleConstants.INDEX_ZERO_2I, FirstOracleConstants.INDEX_ZERO_2I, terrain3DSize, terrain3DShift );
    }
    
    public static RegistrableSceneImpl provide2D( Index2D terrain2DSize, Index2D terrain2DShift ) {
        return provide( terrain2DSize, terrain2DShift, FirstOracleConstants.INDEX_ZERO_3I, FirstOracleConstants.INDEX_ZERO_3I );
    }
    
    public static RegistrableSceneImpl provide(
        Index2D terrain2DSize,
        Index2D terrain2DShift,
        Index3D terrain3DSize,
        Index3D terrain3DShift
    ) {
        return new RegistrableSceneImpl(
            new RegistrableScene2DImpl( terrain2DSize.x(), terrain2DSize.y(), terrain2DShift ),
            new RegistrableScene3DImpl( terrain3DSize.x(), terrain3DSize.y(), terrain3DSize.z(), terrain3DShift ),
            new RegistrableBackgroundImpl(),
            new RegistrableOverlayImpl()
        );
    }
    
    public static RegistrableSceneImpl provideOptimised(
        Index2D terrain2DSize,
        Index2D terrain2DShift,
        Index3D terrain3DSize,
        Index3D terrain3DShift
    ) {
        return new RegistrableSceneImpl(
            new OptimisedRegistrableScene2DImpl( terrain2DSize.x(), terrain2DSize.y(), terrain2DShift ),
            new OptimisedRegistrableScene3DImpl( terrain3DSize.x(), terrain3DSize.y(), terrain3DSize.z(), terrain3DShift ),
            new RegistrableBackgroundImpl(),
            new RegistrableOverlayImpl()
        );
    }
    
    public RegistrableSceneImpl(
        RegistrableScene2D scene2D,
        RegistrableScene3D scene3D,
        RegistrableBackground background,
        RegistrableOverlay overlay
    ) {
        this.scene2D = scene2D;
        this.scene3D = scene3D;
        this.background = background;
        this.overlay = overlay;
    }
    
    private RegistrableScene2D scene2D;
    private RegistrableScene3D scene3D;
    private RegistrableBackground background;
    private RegistrableOverlay overlay;
    
    public void setScene2D( RegistrableScene2D scene2D ) {
        this.scene2D = scene2D;
    }
    
    public void setScene3D( RegistrableScene3D scene3D ) {
        this.scene3D = scene3D;
    }
    
    public void setBackground( RegistrableBackground background ) {
        this.background = background;
    }
    
    public void setOverlay( RegistrableOverlay overlay ) {
        this.overlay = overlay;
    }
    
    @Override
    public RegistrableScene2D getScene2D() {
        return scene2D;
    }
    
    @Override
    public RegistrableScene3D getScene3D() {
        return scene3D;
    }
    
    @Override
    public RegistrableBackground getBackground() {
        return background;
    }
    
    @Override
    public RegistrableOverlay getOverlay() {
        return overlay;
    }
    
    @Override
    public void setBackgroundCamera( Camera2D camera ) {
        background.setBackgroundCamera( camera );
    }
    
    @Override
    public void setBackgroundColour( Colour colour ) {
        background.setBackgroundColour( colour );
    }
    
    @Override
    public void setOverlayCamera( Camera2D camera ) {
        overlay.setOverlayCamera( camera );
    }
    
    @Override
    public void setScene2DCamera( Camera2D camera ) {
        scene2D.setScene2DCamera( camera );
    }
    
    @Override
    public void setScene3DCamera( Camera3D camera ) {
        scene3D.setScene3DCamera( camera );
    }
}
