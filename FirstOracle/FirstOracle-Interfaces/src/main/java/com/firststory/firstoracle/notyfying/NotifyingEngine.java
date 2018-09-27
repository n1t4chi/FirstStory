/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * @author n1t4chi
 */
public class NotifyingEngine {
    
    private static final Executor executor = new NotifyingExecutor();
    private static final Queue< Runnable > commands = new ConcurrentLinkedQueue<>();
    private static Thread thread = null;
    
    public static < Listener, Action extends NotifyAction< Listener > > void notify(
        Collection< Listener > listeners, Action action
    )
    {
        executor.execute( () -> {
            for ( var listener : listeners ) {
                action.notify( listener );
            }
        } );
    }
    
    private static synchronized boolean keepThread() {
        try {
            NotifyingEngine.class.wait( 1000 );
            if ( commands.isEmpty() ) {
                thread = null;
                return false;
            }
        } catch ( InterruptedException ignored ) {
        }
        return true;
    }
    
    private static synchronized void updateThread() {
        if ( thread == null ) {
            newThread();
        } else {
            NotifyingEngine.class.notify();
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
            } while ( keepThread() );
        }, "Notifying Engine" );
        thread.start();
    }
    
    private NotifyingEngine() {
    }
    
    public interface NotifyAction< Listener > {
        void notify( Listener listener );
    }
    
    private static class NotifyingExecutor implements Executor {
        
        @Override
        public void execute( Runnable command ) {
            commands.add( command );
            updateThread();
        }
    }
    
}
