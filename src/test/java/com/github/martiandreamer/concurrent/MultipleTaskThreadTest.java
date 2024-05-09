package com.github.martiandreamer.concurrent;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MultipleTaskThreadTest {
    @Test
    void testCall() {
        MultipleTaskThread thread = new MultipleTaskThread();
        thread.start();
        thread.submit(() -> 3).thenAccept(System.out::println);
        thread.submit(() -> "Hello, World").thenAccept(System.out::println);
        thread.submit(() -> System.out.println("Hello, World 2")).join();
        thread.terminate();
        assertThrows(IllegalStateException.class, () -> thread.submit(() -> "failed"));
        MultipleTaskThread thread2 = new MultipleTaskThread();
        thread2.start();
        thread2.interrupt();
        assertThrows(IllegalStateException.class, () -> thread.submit(() -> "failed"));
    }
}