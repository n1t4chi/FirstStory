/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.optimisation;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.input.SceneParser;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.scene.RegistrableBackgroundImpl;
import com.firststory.firstoracle.scene.RegistrableOverlayImpl;
import com.firststory.firstoracle.scene.RegistrableScene3DImpl;
import com.firststory.firstoracle.scene.RegistrableSceneImpl;

import java.util.List;

/**
 * @author n1t4chi
 */
public class Optimised2D extends BaseApp {
    
    public Optimised2D(
        Terrain2D< ? >[][] terrains2D,
        Index2D terrain2dShift,
        List< PositionableObject2D< ?, ? > > renderables2D
    ) {
        super( terrains2D, terrain2dShift, renderables2D, BaseApp.createOptimisedScene );
    }
    
    public Optimised2D( String text ) {
        super( new RegistrableSceneImpl(
            SceneParser.parseToOptimised( text ).getScene2D(),
            new RegistrableScene3DImpl( FirstOracleConstants.INDEX_ZERO_3I, FirstOracleConstants.INDEX_ZERO_3I ),
            new RegistrableBackgroundImpl(),
            new RegistrableOverlayImpl()
        ) );
    }
}
