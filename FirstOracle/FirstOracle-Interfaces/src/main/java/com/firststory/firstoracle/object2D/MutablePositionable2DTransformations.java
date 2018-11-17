/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.data.Rotation2D;
import com.firststory.firstoracle.data.Scale2D;
import com.firststory.firstoracle.object.MutablePositionableObjectTransformations;

/**
 * @author n1t4chi
 */
public class MutablePositionable2DTransformations
    extends
        Mutable2DTransformations
    implements
        PositionableObject2DTransformations,
        MutablePositionableObjectTransformations< Position2D, Scale2D, Rotation2D >
{
    
    private Position2D position = FirstOracleConstants.POSITION_ZERO_2F;
    
    @Override
    public Position2D getPosition() {
        return position;
    }
    
    public void setPosition( Position2D position ) {
        this.position = position;
    }
    
    public void setPosition( float x, float y ) {
        setPosition( Position2D.pos2( x, y ) );
    }
    
    public void setPositionX( float x ) {
        setPosition( x, position.y() );
    }
    
    public void setPositionY( float y ) {
        setPosition( position.x(), y );
    }
}
