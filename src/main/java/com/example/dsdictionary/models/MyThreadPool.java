package com.example.dsdictionary.models;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyThreadPool {
    private final int poolSize;// The number of threads in the pool
    private final Thread[] workers;// Array to store worker threads
    private final BlockingQueue<Runnable> taskQueue;// Queue to store pending tasks

    private volatile boolean isShutdownInitiated = false;// Flag indicating whether the shutdown process has been initiated



    public MyThreadPool(int poolSize) {
        this.poolSize = poolSize;
        taskQueue = new LinkedBlockingQueue<>();
        workers = new Thread[poolSize];

        // Initialize and start the worker threads
        for (int i = 0; i < poolSize; i++) {
            workers[i] = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()&&!isShutdownInitiated) {//只要线程没有被中断
                        try {
                            // Wait for and retrieve the next task from the queue
                            Runnable task = taskQueue.take();
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

    // Method to submit a new task for execution
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


    // Method to initiate a graceful shutdown of the thread pool
    public void shutdown() {
        isShutdownInitiated = true; // Mark that shutdown has been initiated

        // Wait for all tasks to complete
        while (!taskQueue.isEmpty()) {
            try {
                Thread.sleep(100); // Pause to reduce CPU usage during wait
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Interrupt all worker threads to stop processing
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }
}
