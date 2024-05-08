package com.github.martiandreamer.concurrent;

import org.junit.Test;

import static org.junit.Assert.*;

public class MultipleTaskThreadTest {
    @Test
    public void testCall() {
        MultipleTaskThread thread = new MultipleTaskThread();
        thread.start();
        thread.submit(() -> 3).thenAccept(System.out::println);
        thread.submit(() -> "Hello, World").thenAccept(System.out::println);
        thread.submit(() -> System.out.println("Hello, World 2")).join();
        thread.terminate();
        assertThrows(IllegalStateException.class, () -> thread.submit(() -> "failed"));
    }
}