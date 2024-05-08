package com.github.martiandreamer.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MartianExecutorService implements ExecutorService {

    private final ConcurrentLinkedQueue<Task<?>> tasks = new ConcurrentLinkedQueue<>();

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return List.of();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Task<T> t = new Task<>(task);
        tasks.offer(t);
        return t.future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        RunnableTask runnableTask = new RunnableTask(task);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        RunnableTask runnableTask = new RunnableTask(task);
        return runnableTask.future;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return List.of();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return List.of();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    @Override
    public void execute(Runnable command) {

    }

    private static class Task<T> {
        protected Callable<T> callable;
        protected CompletableFuture<T> future;
        protected Task(Callable<T> callable) {
            this.callable = callable;
            this.future = new CompletableFuture<>();
        }
    }

    private static class RunnableTask extends Task<Void> {
        protected RunnableTask(Runnable runnable) {
            super(() -> {
                runnable.run();
                return null;
            });
        }
    }
}
