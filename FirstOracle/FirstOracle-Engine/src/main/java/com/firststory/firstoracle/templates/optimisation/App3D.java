/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.optimisation;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;

import java.util.ArrayList;

/**
 * @author n1t4chi
 */
public class App3D {
    
    public static void main( String[] args ) {
        var terrains3D = new Terrain3D[200][20][200];
        var terrain3dShift = Index3D.id3( 0, 0, 0 );
        var renderables3D = new ArrayList< PositionableObject3D< ?, ? > >( 1000 );
        
        Utils.getApp3D( terrains3D, terrain3dShift, renderables3D ).run();
    }
}
