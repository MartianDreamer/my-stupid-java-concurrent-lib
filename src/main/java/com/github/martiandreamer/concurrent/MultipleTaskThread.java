package com.github.martiandreamer.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultipleTaskThread extends Thread {
    private boolean terminated;
    private final ConcurrentLinkedQueue<CallableTask<?>> taskQueue;

    public MultipleTaskThread() {
        this(new ConcurrentLinkedQueue<>());
    }

    MultipleTaskThread(ConcurrentLinkedQueue<CallableTask<?>> taskQueue) {
        super();
        this.taskQueue = taskQueue;
    }

    public void terminate() {
        this.terminated = true;
    }

    public <T> CompletableFuture<T> submit(Callable<T> callable) {
        if (this.terminated || !isAlive()) {
            throw new IllegalStateException("MultipleReturnTaskThread terminated or not alive");
        }
        CallableTask<T> task = new CallableTask<>(callable);
        taskQueue.offer(task);
        return task.getFuture();
    }

    public CompletableFuture<Void> submit(Runnable task) {
        return this.submit(() -> {
            task.run();
            return null;
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void run() {
        while (!terminated && !isInterrupted() && isAlive()) {
            CallableTask task = taskQueue.poll();
            if (task == null) {
                continue;
            }
            try {
                if (!task.getFuture().isCancelled()) {
                    task.getFuture().complete(task.getCallable().call());
                }
            } catch (Exception e) {
                if (!task.getFuture().isCancelled()) {
                    task.getFuture().completeExceptionally(e);
                }
            }
        }
    }
}
