/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.BoundingBox;
import com.firststory.firstoracle.object.data.Position2D;
import org.joml.Vector2fc;

import java.util.List;

import static java.lang.Math.*;

/**
 * @author n1t4chi
 */
public class BoundingBox2D implements BoundingBox< BoundingBox2D, Object2DTransformations, Vector2fc > {
    
    public static BoundingBox2D getBoundingBox2D( List< Position2D > vertices ) {
        float minX, maxX, minY, maxY;
        minX = minY = Float.MAX_VALUE;
        maxY = maxX = -Float.MAX_VALUE;
        for ( var vertex : vertices ) {
            var x = vertex.x();
            var y = vertex.y();
            minX = min( minX, x );
            maxX = max( maxX, x );
            minY = min( minY, y );
            maxY = max( maxY, y );
        }
        return new BoundingBox2D( minX, maxX, minY, maxY );
    }
    
    public static BoundingBox2D getBoundingBox2D( List< Position2D >[] verticesArray ) {
        float minX, maxX, minY, maxY;
        minX = minY = Float.MAX_VALUE;
        maxY = maxX = -Float.MAX_VALUE;
        for ( var vertices : verticesArray ) {
            for ( var vertex : vertices ) {
                var x = vertex.x();
                var y = vertex.y();
                minX = min( minX, x );
                maxX = max( maxX, x );
                minY = min( minY, y );
                maxY = max( maxY, y );
            }
        }
        return new BoundingBox2D( minX, maxX, minY, maxY );
    }
    
    private final float minX, maxX, minY, maxY;
    
    public BoundingBox2D( float minX, float maxX, float minY, float maxY ) {
        this.minX = ( minX < maxX ) ? minX : maxX;
        this.maxX = ( minX > maxX ) ? minX : maxX;
        this.minY = ( minY < maxY ) ? minY : maxY;
        this.maxY = ( minY > maxY ) ? minY : maxY;
    }
    
    public float getMaxX() {
        return maxX;
    }
    
    public float getMaxY() {
        return maxY;
    }
    
    public float getMinX() {
        return minX;
    }
    
    public float getMinY() {
        return minY;
    }
    
    public BoundingBox2D getTransformedBoundingBox( PositionableObject2DTransformations transformations ) {
        return getTransformedBoundingBox( transformations, transformations.getPosition() );
    }
    public BoundingBox2D getTransformedBoundingBox( Object2DTransformations transformations, Vector2fc position ) {
        float minX, maxX, minY, maxY;
        minX = this.minX;
        maxX = this.maxX;
        minY = this.minY;
        maxY = this.maxY;
        var rotation = transformations.getRotation();
        var scale = transformations.getScale();
        
        minX *= scale.x();
        maxX *= scale.x();
        minY *= scale.y();
        maxY *= scale.y();
        
        if ( rotation != 0 ) {
            var rot = toRadians( rotation );
            var sinZ = ( float ) sin( rot );
            var cosZ = ( float ) cos( rot );
    
            var x = minX;
            var y = minY;
            minX = ( x * cosZ ) - ( y * sinZ );
            minY = ( x * sinZ ) + ( y * cosZ );
            x = maxX;
            y = maxY;
            maxX = ( x * cosZ ) - ( y * sinZ );
            maxY = ( x * sinZ ) + ( y * cosZ );
        }
        
        minX += position.x();
        maxX += position.x();
        minY += position.y();
        maxY += position.y();
        return new BoundingBox2D( minX, maxX, minY, maxY );
    }
    
    @Override
    public int hashCode() {
        var result = ( minX != +0.0f ? Float.floatToIntBits( minX ) : 0 );
        result = 31 * result + ( maxX != +0.0f ? Float.floatToIntBits( maxX ) : 0 );
        result = 31 * result + ( minY != +0.0f ? Float.floatToIntBits( minY ) : 0 );
        result = 31 * result + ( maxY != +0.0f ? Float.floatToIntBits( maxY ) : 0 );
        return result;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
    
        var that = ( BoundingBox2D ) o;
        
        return Float.compare( that.minX, minX ) == 0 && Float.compare( that.maxX, maxX ) == 0 &&
            Float.compare( that.minY, minY ) == 0 && Float.compare( that.maxY, maxY ) == 0;
    }
    
    @Override
    public String toString() {
        return "BBox2D: [" + minX + "," + maxX + "] [" + minY + "," + maxY + "]";
    }
    
    /**
     * Returns whether the given bounding box intersects with this bounding box
     *
     * @param boundingBox2D bounding box to check intersection with
     *
     * @return true if they intersect
     */
    public boolean intersects( BoundingBox2D boundingBox2D ) {
        return minX <= boundingBox2D.maxX && maxX >= boundingBox2D.minX &&
            minY <= boundingBox2D.maxY && maxY >= boundingBox2D.minY;
    }
}
