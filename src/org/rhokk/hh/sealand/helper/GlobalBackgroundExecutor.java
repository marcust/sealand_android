package org.rhokk.hh.sealand.helper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GlobalBackgroundExecutor {

    private final static Executor SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

    public static void execute( Runnable runnable ) {
        SINGLE_THREAD_EXECUTOR.execute( runnable );
    }
    
    
}
