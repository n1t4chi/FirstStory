/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.notifying;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * @author n1t4chi
 */
public class NotyfyingEngine {
    
    private static final Executor executor = new NotyfingExecutor();
    private static final Queue< Runnable > commands = new ConcurrentLinkedQueue<>();
    private static Thread thread = null;
    
    public static < Listener, Action extends NotifyAction< Listener > > void notify(
        Collection< Listener > listeners, Action action
    )
    {
        executor.execute( () -> {
            for ( Listener listener : listeners ) {
                action.notify( listener );
            }
        } );
    }
    
    private static synchronized boolean removeThread() {
        try {
            NotyfyingEngine.class.wait( 10000 );
            if ( commands.isEmpty() ) {
                thread = null;
                return true;
            }
        } catch ( InterruptedException ignored ) {
        }
        return false;
    }
    
    private static synchronized void updateThread() {
        if ( thread == null ) {
            newThread();
        } else {
            NotyfyingEngine.class.notify();
            if ( thread == null ) {
                newThread();
            }
        }
    }
    
    private static void newThread() {
        thread = new Thread( () -> {
            do {
                while ( !commands.isEmpty() ) {
                    try {
                        commands.poll().run();
                    } catch ( Exception ex ) {
                        System.err.println(
                            "Exception in notification engine.\nCaused by:" + ex.getLocalizedMessage() );
                        ex.printStackTrace();
                    }
                }
            } while ( !removeThread() );
        } );
        thread.start();
    }
    
    private NotyfyingEngine() {
    }
    
    public interface NotifyAction< Listener > {
        void notify( Listener listener );
    }
    
    private static class NotyfingExecutor implements Executor {
        
        @Override
        public void execute( Runnable command ) {
            commands.add( command );
            updateThread();
        }
    }
    
}
