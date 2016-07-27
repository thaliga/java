package org.th.spring.customscope;

/**
 * Scoped runnable (registering to/unregistering from given scope).
 */
class ScopedRunnable extends ScopedWrapper<Void> implements Runnable {

    private final Runnable runnable;

    ScopedRunnable(Runnable runnable) {
        this(null, runnable);
    }

    ScopedRunnable(CustomScopeKey contextKey, Runnable runnable) {
        super(contextKey);
        this.runnable = runnable;
    }

    @Override
    public void run() {
        execute();
    }

    @Override
    protected Void internalExecute() {
        runnable.run();
        return null;
    }
}
