package com.github.martiandreamer.concurrent;

import java.util.ArrayList;
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
import java.util.stream.Collectors;

public class MartianExecutorService implements ExecutorService {

    private final ConcurrentLinkedQueue<CallableTask<?>> tasks = new ConcurrentLinkedQueue<>();

    private final List<MultipleTaskThread> multipleTaskThreads = new ArrayList<>();

    private MartianExecutorService() {
    }

    public static ExecutorService fixedPool(int numThreads) {
        ArrayList<MultipleTaskThread> multipleTaskThreads = new ArrayList<>(numThreads);
        MartianExecutorService executorService = new MartianExecutorService();
        for (int i = 0; i < numThreads; i++) {
            MultipleTaskThread thread = new MultipleTaskThread(executorService.tasks);
            thread.start();
            executorService.multipleTaskThreads.add(thread);
        }
        return executorService;
    }

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
        CallableTask<T> t = new CallableTask<>(task);
        tasks.offer(t);
        return t.getFuture();
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        CallableTask<T> callableTask =  new CallableTask<>(() -> {
            task.run();
            return result;
        });
        tasks.offer(callableTask);
        return callableTask.getFuture();
    }

    @Override
    public Future<?> submit(Runnable task) {
        RunnableTask runnableTask = new RunnableTask(task);
        tasks.offer(runnableTask);
        return runnableTask.getFuture();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        List<CallableTask<T>> callableTasks = tasks.stream()
                .map(CallableTask::new)
                .toList();
        this.tasks.addAll(callableTasks);
        return callableTasks.stream()
                .map(CallableTask::getFuture)
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        List<CallableTask<T>> callableTasks = tasks.stream()
                .map(e -> new CallableTask<T>(e, timeout, unit))
                .toList();
        this.tasks.addAll(callableTasks);
        return callableTasks.stream()
                .map(CallableTask::getFuture)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        List<CallableTask<T>> callableTasks = tasks.stream()
                .map(CallableTask::new)
                .toList();
        this.tasks.addAll(callableTasks);
        CompletableFuture[] futures = callableTasks.stream()
                .map(CallableTask::getFuture)
                .toArray(CompletableFuture[]::new);
        Object rs =  CompletableFuture.anyOf(futures).get();
        this.tasks.removeAll(callableTasks);
        return (T) rs;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        List<CallableTask<T>> callableTasks = tasks.stream()
                .map(e-> new CallableTask<T>(e, timeout, unit))
                .toList();
        this.tasks.addAll(callableTasks);
        CompletableFuture[] futures = callableTasks.stream()
                .map(CallableTask::getFuture)
                .toArray(CompletableFuture[]::new);
        Object rs =  CompletableFuture.anyOf(futures).get();
        this.tasks.removeAll(callableTasks);
        return (T) rs;
    }

    @Override
    public void execute(Runnable command) {
        submit(command);
    }
}
