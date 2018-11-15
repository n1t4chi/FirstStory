/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.optimisation;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.input.SceneParser;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.scene.RegistrableBackgroundImpl;
import com.firststory.firstoracle.scene.RegistrableOverlayImpl;
import com.firststory.firstoracle.scene.RegistrableScene2DImpl;
import com.firststory.firstoracle.scene.RegistrableSceneImpl;

import java.util.List;

/**
 * @author n1t4chi
 */
public class NonOptimised3D extends BaseApp {
    
    public NonOptimised3D(
        Terrain3D< ? >[][][] terrains3D,
        Index3D terrain3dShift,
        List< PositionableObject3D< ?, ? > > renderables3D
    ) {
        super( terrains3D, terrain3dShift, renderables3D, BaseApp.createNonOptimisedScene );
    }
    
    public NonOptimised3D( String text ) {
        super( new RegistrableSceneImpl(
            new RegistrableScene2DImpl( FirstOracleConstants.INDEX_ZERO_2I, FirstOracleConstants.INDEX_ZERO_2I ),
            SceneParser.parseToNonOptimised( text ).getScene3D(),
            new RegistrableBackgroundImpl(),
            new RegistrableOverlayImpl()
        ) );
    }
}
