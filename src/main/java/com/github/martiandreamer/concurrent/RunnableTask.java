package com.github.martiandreamer.concurrent;


class RunnableTask extends CallableTask<Void> {
    protected RunnableTask(Runnable runnable) {
        super(() -> {
            runnable.run();
            return null;
        });
    }
}
