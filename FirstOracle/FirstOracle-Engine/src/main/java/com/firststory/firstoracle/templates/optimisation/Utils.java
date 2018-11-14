/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.optimisation;

import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;

import java.util.List;

/**
 * @author n1t4chi
 */
public interface Utils {
    String USE_OPTIMISED_PROPERTY = "optimised";
    
    static BaseApp getApp2D(
        Terrain2D< ? >[][] terrains2D,
        Index2D terrain2dShift,
        List< PositionableObject2D< ?, ? > > renderables2D
    ) {
        if( isOptimised() ) {
            return new Optimised2D( terrains2D, terrain2dShift, renderables2D );
        } else {
            return new NonOptimised2D( terrains2D, terrain2dShift, renderables2D );
        }
        
    }
    static BaseApp getApp3D(
        Terrain3D< ? >[][][] terrains3D,
        Index3D terrain3dShift,
        List< PositionableObject3D< ?, ? > > renderables3D
    ) {
        if( isOptimised() ) {
            return new Optimised3D( terrains3D, terrain3dShift, renderables3D );
        } else {
            return new NonOptimised3D( terrains3D, terrain3dShift, renderables3D );
        }
        
    }
    
    static boolean isOptimised() {
        return PropertiesUtil.isPropertyTrue( USE_OPTIMISED_PROPERTY );
    }
    
}