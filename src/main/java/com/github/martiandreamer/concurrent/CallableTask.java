package com.github.martiandreamer.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class CallableTask<T> {
    private final Callable<T> callable;
    private final CompletableFuture<T> future;
    protected CallableTask(Callable<T> callable) {
        this.callable = callable;
        this.future = new CompletableFuture<>();
    }

    protected CallableTask(Callable<T> callable, long timeout, TimeUnit unit) {
        this.callable = callable;
        this.future = new CompletableFuture<T>()
                .orTimeout(timeout, unit);
    }

    public Callable<T> getCallable() {
        return callable;
    }

    public CompletableFuture<T> getFuture() {
        return future;
    }
}
