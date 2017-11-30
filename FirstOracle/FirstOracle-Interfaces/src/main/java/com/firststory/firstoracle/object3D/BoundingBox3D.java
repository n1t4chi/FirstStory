/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.BoundingBox;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public class BoundingBox3D implements BoundingBox< BoundingBox3D, ObjectTransformations3D > {
    
    public static BoundingBox3D getBoundingBox3D( float[] vertices ) {
        float minX, maxX, minY, maxY, minZ, maxZ;
        minX = minY = minZ = Float.MAX_VALUE;
        maxZ = maxY = maxX = -Float.MAX_VALUE;
        for ( int i = 0; i < vertices.length; i += 3 ) {
            if ( vertices[ i ] < minX ) {
                minX = vertices[ i ];
            }
            if ( vertices[ i ] > maxX ) {
                maxX = vertices[ i ];
            }
            if ( vertices[ i + 1 ] < minY ) {
                minY = vertices[ i + 1 ];
            }
            if ( vertices[ i + 1 ] > maxY ) {
                maxY = vertices[ i + 1 ];
            }
            if ( vertices[ i + 2 ] < minZ ) {
                minZ = vertices[ i + 2 ];
            }
            if ( vertices[ i + 2 ] > maxZ ) {
                maxZ = vertices[ i + 2 ];
            }
        }
        return new BoundingBox3D( minX, maxX, minY, maxY, minZ, maxZ );
    }
    
    public static BoundingBox3D getBoundingBox3D( float[][] verticesArray ) {
        float minX, maxX, minY, maxY, minZ, maxZ;
        minX = minY = minZ = Float.MAX_VALUE;
        maxZ = maxY = maxX = -Float.MAX_VALUE;
        for ( float[] vertices : verticesArray ) {
            for ( int i = 0; i < vertices.length; i += 3 ) {
                if ( vertices[ i ] < minX ) {
                    minX = vertices[ i ];
                }
                if ( vertices[ i ] > maxX ) {
                    maxX = vertices[ i ];
                }
                if ( vertices[ i + 1 ] < minY ) {
                    minY = vertices[ i + 1 ];
                }
                if ( vertices[ i + 1 ] > maxY ) {
                    maxY = vertices[ i + 1 ];
                }
                if ( vertices[ i + 2 ] < minZ ) {
                    minZ = vertices[ i + 2 ];
                }
                if ( vertices[ i + 2 ] > maxZ ) {
                    maxZ = vertices[ i + 2 ];
                }
            }
        }
        return new BoundingBox3D( minX, maxX, minY, maxY, minZ, maxZ );
    }
    
    private final float minX, maxX, minY, maxY, minZ, maxZ;
    
    public BoundingBox3D( float minX, float maxX, float minY, float maxY, float minZ, float maxZ ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }
    
    @Override
    public int hashCode() {
        int result = ( minX != +0.0f ? Float.floatToIntBits( minX ) : 0 );
        result = 31 * result + ( maxX != +0.0f ? Float.floatToIntBits( maxX ) : 0 );
        result = 31 * result + ( minY != +0.0f ? Float.floatToIntBits( minY ) : 0 );
        result = 31 * result + ( maxY != +0.0f ? Float.floatToIntBits( maxY ) : 0 );
        result = 31 * result + ( minZ != +0.0f ? Float.floatToIntBits( minZ ) : 0 );
        result = 31 * result + ( maxZ != +0.0f ? Float.floatToIntBits( maxZ ) : 0 );
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
        
        BoundingBox3D that = ( BoundingBox3D ) o;
        
        return Float.compare( that.minX, minX ) == 0 && Float.compare( that.maxX, maxX ) == 0 &&
            Float.compare( that.minY, minY ) == 0 && Float.compare( that.maxY, maxY ) == 0 &&
            Float.compare( that.minZ, minZ ) == 0 && Float.compare( that.maxZ, maxZ ) == 0;
    }
    
    @Override
    public String toString() {
        return "BBox3D: " + "[" + minX + "," + maxX + "] " + "[" + minY + "," + maxY + "] " + "[" +
            minZ + "," + maxZ + "]";
    }
    
    public float getMaxX() {
        return maxX;
    }
    
    public float getMaxY() {
        return maxY;
    }
    
    public float getMaxZ() {
        return maxZ;
    }
    
    public float getMinX() {
        return minX;
    }
    
    public float getMinY() {
        return minY;
    }
    
    public float getMinZ() {
        return minZ;
    }
    
    public BoundingBox3D getTransformedBoundingBox( ObjectTransformations3D transformations ) {
        float minX, maxX, minY, maxY, minZ, maxZ;
        minX = this.minX;
        maxX = this.maxX;
        minY = this.minY;
        maxY = this.maxY;
        minZ = this.minZ;
        maxZ = this.maxZ;
        Vector3fc rotation = transformations.getRotation();
        Vector3fc position = transformations.getPosition();
        Vector3fc scale = transformations.getScale();
        if ( rotation.lengthSquared() != 0 ) {
            minX *= 1.5;
            maxX *= 1.5;
            minY *= 1.5;
            maxY *= 1.5;
            minZ *= 1.5;
            maxZ *= 1.5;
        }
        minX *= scale.x();
        minX += position.x();
        maxX *= scale.x();
        maxX += position.y();
        minY *= scale.y();
        minY += position.y();
        maxY *= scale.y();
        maxY += position.y();
        minZ *= scale.z();
        minZ += position.z();
        maxZ *= scale.z();
        maxZ += position.z();
        return new BoundingBox3D( minX, maxX, minY, maxY, minZ, maxZ );
    }
}
