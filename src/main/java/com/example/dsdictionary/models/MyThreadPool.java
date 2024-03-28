package com.example.dsdictionary.models;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyThreadPool {
    private final int poolSize;
    private final Thread[] workers;//存储工作线程的数组
    private final BlockingQueue<Runnable> taskQueue;//存储待执行任务的阻塞队列

    private volatile boolean isShutdownInitiated = false; // 标志位，表示是否启动了关闭过程



    public MyThreadPool(int poolSize) {
        this.poolSize = poolSize;
        taskQueue = new LinkedBlockingQueue<>();
        workers = new Thread[poolSize];

        for (int i = 0; i < poolSize; i++) {
            workers[i] = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()&&!isShutdownInitiated) {//只要线程没有被中断
                        try {
                            Runnable task = taskQueue.take();//等待直到任务队列中有可用的任务
                            task.run();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            });
            workers[i].start();
        }
    }

    public void execute(Runnable task) {
        if (!isShutdownInitiated) {
            try {
                taskQueue.put(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            throw new IllegalStateException("ThreadPool is shutdown, cannot accept new tasks");
        }
    }


    public void shutdown() {
        isShutdownInitiated = true; // 设置关闭标志

        // 等待所有任务完成
        while (!taskQueue.isEmpty()) {
            try {
                Thread.sleep(100); // 短暂等待，避免过于频繁的检查
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 中断所有工作线程
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }
}
