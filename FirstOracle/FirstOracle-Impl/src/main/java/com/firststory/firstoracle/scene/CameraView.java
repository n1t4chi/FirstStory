/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.object.BoundingBox;
import org.joml.*;

import java.lang.Math;

/**
 * @author n1t4chi
 */
abstract class CameraView {
    
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
        invMatrix = camera.getMatrixRepresentation().invert( new Matrix4f() ).scale( 1.1f );
    
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

    abstract float dim1( Vector3fc vector );

    abstract float dim2( Vector3fc vector );

    abstract float minDim1Box( BoundingBox< ?, ?, ? > box );

    abstract float maxDim1Box( BoundingBox< ?, ?, ? > box );

    abstract float minDim2Box( BoundingBox< ?, ?, ? > box );

    abstract float maxDim2Box( BoundingBox< ?, ?, ? > box );

    abstract float minDimOtherBox( BoundingBox< ?, ?, ? > box );

    abstract float maxDimOtherBox( BoundingBox< ?, ?, ? > box );
    
    abstract Vector3fc pointAtDimOther( Line3D line, float dimOther );
    
    @Override
    public String toString() {
        return "CameraView{ hl=" + highLeft + ", r=" + right + ", lr=" + lowRight + ", l=" + left + '}';
    }
    
    Plane getPlaneForDimOther( float dimOther ) {
        return new Plane(
            pointAtDimOther( highLeft, dimOther ),
            pointAtDimOther( right, dimOther ),
            pointAtDimOther( lowRight, dimOther ),
            pointAtDimOther( left, dimOther ),
            this::dim1,
            this::dim2
        );
    }
    
    boolean shouldDisplay( BoundingBox< ?, ?, ? > box ) {
        return insideRectangleAtOther( box, minDimOtherBox( box ) ) || insideRectangleAtOther( box, maxDimOtherBox( box ) );
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
    
    private boolean insideRectangleAtOther( BoundingBox< ?, ?, ? > box, float dimOther ) {
        var l = dim1( pointAtDimOther( left, dimOther ) );
        var r = dim1( pointAtDimOther( right, dimOther ) );
        var lr = dim2( pointAtDimOther( lowRight, dimOther ) );
        var hl = dim2( pointAtDimOther( highLeft, dimOther ) );
        var minDim1 = Math.min( l, r );
        var maxDim1 = Math.max( l, r );
        var minDim2 = Math.min( lr, hl );
        var maxDim2 = Math.max( lr, hl );
        
        return FirstOracleConstants.objectWithinBoundary(
            minDim1Box( box ),
            maxDim1Box( box ),
            minDim2Box( box ),
            maxDim2Box( box ),
            minDim1,
            maxDim1,
            minDim2,
            maxDim2
        );
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
        return updateHightLeftIfPreviousIsValid( lines, selectHighLeftIndexBasedOnDim2( lines ) );
    }
    
    private int updateHightLeftIfPreviousIsValid( Line3D[] lines, int highLeftIndex ) {
        var prev = getPrevIndex( lines, highLeftIndex );
        if( sameDim2( lines[ highLeftIndex ], lines[ prev ] ) && furtherOnDim1( lines[ highLeftIndex ], lines[ prev ] ) ) {
            highLeftIndex = prev;
        }
        return highLeftIndex;
    }
    
    private boolean furtherOnDim1( Line3D line, Line3D line1 ) {
        return dim1( line.getPoint0() ) > dim1( line1.getPoint0() );
    }
    
    private boolean sameDim2( Line3D line, Line3D line1 ) {
        return FirstOracleConstants.isReallyClose( dim2( line.getPoint0() ), dim2( line1.getPoint0() ) );
    }
    
    private int getPrevIndex( Line3D[] lines, int highLeftIndex ) {
        return ( highLeftIndex - 1 + 4 ) % lines.length;
    }
    
    private int getNextIndex( Line3D[] lines, int highLeftIndex ) {
        return ( highLeftIndex + 1 ) % lines.length;
    }
    
    private int selectHighLeftIndexBasedOnDim2( Line3D[] lines ) {
        var highLeftIndex = 0;
        var maxDim2 = dim2( lines[0].getPoint0() );
        for( var it = 1; it< lines.length; it ++ ) {
            var dim2 = this.dim2( lines[ it ].getPoint0() );
            if( maxDim2 < dim2 ) {
                highLeftIndex = it;
                maxDim2 = dim2;
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
}
