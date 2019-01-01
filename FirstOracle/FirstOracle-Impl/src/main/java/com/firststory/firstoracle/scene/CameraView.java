/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.Camera;
import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.BoundingBox;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author n1t4chi
 */
class CameraView {
    private final Matrix4f invMatrix;
    
    /*
    points reference in space
    hl____r
    |     |
    |l__lr|
      hl
     /  \
    l    r
     \  /
      lr
    
     */
    private final Line3D highLeft;
    private final Line3D right;
    private final Line3D lowRight;
    private final Line3D left;
    
    CameraView( Camera camera ) {
        invMatrix = camera.getMatrixRepresentation().invert( new Matrix4f() );
        
        var lines = new Line3D[]{
            /*UpRight*/ createLine( 1, 1 ),
            /*DownRight*/ createLine( 1, -1 ),
            /*DownLeft*/ createLine( -1, -1 ),
            /*upLeft*/ createLine( -1, 1 ),
        };
    
        var highLeftIndex = selectHighLeftIndexAndSwapNeighboursIfNeeded( lines );
        highLeft = lines[ highLeftIndex ];
        right = lines[ ( highLeftIndex + 1 ) % lines.length ];
        lowRight = lines[ ( highLeftIndex + 2 ) % lines.length ];
        left = lines[ ( highLeftIndex + 3 ) % lines.length ];
    }
    
    public Plane getPlaneForZ( int z ) {
        return new Plane(
            highLeft.getPointAtZ( z ),
            right.getPointAtZ( z ),
            lowRight.getPointAtZ( z ),
            left.getPointAtZ( z )
        );
    }
    
    boolean shouldDisplay( BoundingBox< ?, ?, ? > box ) {
        return insideRectangleAtZ( box, box.getMinZ() ) || insideRectangleAtZ( box, box.getMaxZ() );
    }
    
    private boolean insideRectangleAtZ( BoundingBox< ?, ?, ? > box, float z ) {
        return FirstOracleConstants.objectWithinBoundary(
            box.getMinX(),
            box.getMaxX(),
            box.getMinY(),
            box.getMaxY(),
            left.getPointAtZ( z ).x(),
            right.getPointAtZ( z ).x(),
            lowRight.getPointAtZ( z ).y(),
            highLeft.getPointAtZ( z ).y()
        );
    }
    
    Line3D getHighLeft() {
        return highLeft;
    }
    
    Line3D getRight() {
        return right;
    }
    
    Line3D getLowRight() {
        return lowRight;
    }
    
    Line3D getLeft() {
        return left;
    }
    
    
    
    private int selectHighLeftIndexAndSwapNeighboursIfNeeded( Line3D[] lines ) {
        var highLeftIndex = selectHighLeftIndex( lines );
        swapPrevAndNextIfNeeded( lines, highLeftIndex );
        return highLeftIndex;
    }
    
    private void swapPrevAndNextIfNeeded( Line3D[] lines, int highLeftIndex ) {
        var prev = getPrevIndex( lines, highLeftIndex );
        var next = getNextIndex( lines, highLeftIndex );
        if( lines[ prev ].toRightOf( lines[ next ] ) ) {
            var copy = lines[ prev ];
            lines[ prev ] = lines[ next ];
            lines[ next ] = copy;
        }
    }
    
    private int selectHighLeftIndex( Line3D[] lines ) {
        return updateHightLeftIfPreviousIsValid( lines, selectHighLeftIndexBasedOnY( lines ) );
    }
    
    private int updateHightLeftIfPreviousIsValid( Line3D[] lines, int highLeftIndex ) {
        var prev = getPrevIndex( lines, highLeftIndex );
        if( lines[ highLeftIndex ].sameY( lines[ prev ] ) &&
            lines[ highLeftIndex ].toRightOf( lines[ prev ] )
        ) {
            highLeftIndex = prev;
        }
        return highLeftIndex;
    }
    
    private int getPrevIndex( Line3D[] lines, int highLeftIndex ) {
        return ( highLeftIndex - 1 + 4 ) % lines.length;
    }
    
    private int getNextIndex( Line3D[] lines, int highLeftIndex ) {
        return ( highLeftIndex + 1 ) % lines.length;
    }
    
    private int selectHighLeftIndexBasedOnY( Line3D[] lines ) {
        var highLeftIndex = 0;
        var maxY = lines[0].getPoint0().y();
        for( var it = 1; it< lines.length; it ++ ) {
            var y = lines[ it ].getPoint0().y();
            if( maxY < y ) {
                highLeftIndex = it;
                maxY = y;
            }
        }
        return highLeftIndex;
    }
    
    private Line3D createLine( int x, int y ) {
        return new Line3D(
            invertTo3D( invMatrix, new Vector4f( x, y, 0, 1 ) ),
            invertTo3D( invMatrix, new Vector4f( x, y, 1, 1 ) )
        );
    }
    
    private Vector3f invertTo3D( Matrix4f invMatrix, Vector4f in ) {
        var vec4 = in.mul( invMatrix );
        return new Vector3f( vec4.x(), vec4.y(), vec4.z() );
    }
    
    @Override
    public String toString() {
        return "CameraView{ hl=" + highLeft + ", r=" + right + ", lr=" + lowRight + ", l=" + left + '}';
    }
}
