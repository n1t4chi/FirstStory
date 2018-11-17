/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.data.Rotation3D;
import com.firststory.firstoracle.data.Scale3D;
import com.firststory.firstoracle.object.MutablePositionableObjectTransformations;

/**
 * @author n1t4chi
 */
public class MutablePositionable3DTransformations
    extends
        Mutable3DTransformations
    implements
        PositionableObject3DTransformations,
        MutablePositionableObjectTransformations< Position3D, Scale3D, Rotation3D >
{
    
    private Position3D position = FirstOracleConstants.POSITION_ZERO_3F;
    
    @Override
    public Position3D getPosition() {
        return position;
    }
    
    public void setPosition( Position3D position ) {
        this.position = position;
    }
    
    public void setPosition( float x, float y, float z ) {
        setPosition( Position3D.pos3( x, y, z ) );
    }
    
    public void setPositionX( float x ) {
        setPosition( x, position.y(), position.z() );
    }
    
    public void setPositionY( float y ) {
        setPosition( position.x(), y, position.z() );
    }
    
    public void setPositionZ( float z ) {
        setPosition( position.x(), position.y(), z );
    }
}
