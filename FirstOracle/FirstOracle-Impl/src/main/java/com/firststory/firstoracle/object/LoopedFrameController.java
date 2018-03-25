/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author n1t4chi
 */
public class LoopedFrameController extends SingleAnimationFrameController {
    
    private double animationStart;
    private int frames;
    private double duration;
    
    public void setAnimationStart( double animationStart ) {
        this.animationStart = animationStart;
    }
    
    public void setCurrentState( int frames, double animationStart, double duration ) {
        this.animationStart = animationStart;
        this.frames = frames;
        this.duration = duration;
    }
    
    @Override
    public int getCurrentFrame( double lastFrameUpdate ) {
        double nextAnimationStart = animationStart + duration;
        if ( lastFrameUpdate > nextAnimationStart ) {
            setAnimationStart( nextAnimationStart );
        }
        return boundReturnedFrame( ( int ) ( ( lastFrameUpdate - animationStart ) * frames / duration ) );
    }
    
    public int boundReturnedFrame( int frame ) {
        return max( 0, min( frames - 1, frame ) );
    }
}
