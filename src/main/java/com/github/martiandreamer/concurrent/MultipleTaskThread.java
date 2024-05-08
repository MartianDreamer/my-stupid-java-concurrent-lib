package com.github.martiandreamer.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class MultipleTaskThread extends Thread {
    @SuppressWarnings("rawtypes")
    private Callable callable;
    @SuppressWarnings("rawtypes")
    private CompletableFuture resultFuture;
    private boolean terminated;

    public void terminate() {
        this.terminated = true;
    }

    public synchronized <T> CompletableFuture<T> submit(Callable<T> callable) {
        if (this.terminated || !isAlive()) {
            throw new IllegalStateException("MultipleReturnTaskThread terminated or not alive");
        }
        if (this.callable == null) {
            this.callable = callable;
            CompletableFuture<T> rs = new CompletableFuture<>();
            this.resultFuture = rs;
            return rs;
        }
        return null;
    }

    public synchronized CompletableFuture<Void> submit(Runnable task) {
        if (this.terminated || !isAlive()) {
            throw new IllegalStateException("MultipleReturnTaskThread terminated or not alive");
        }
        if (this.callable == null) {
            this.callable = () -> {
                task.run();
                return null;
            };
            CompletableFuture<Void> rs = new CompletableFuture<>();
            this.resultFuture = rs;
            return rs;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        while (!terminated && isAlive()) {
            if (isInterrupted()) {
                interrupt();
            }
            synchronized (this) {
                try {
                    resultFuture.complete(callable.call());
                } catch (Exception e) {
                    if (resultFuture != null) {
                        resultFuture.completeExceptionally(e);
                    }
                } finally {
                    this.callable = null;
                    this.resultFuture = null;
                }
            }
        }
    }
}
