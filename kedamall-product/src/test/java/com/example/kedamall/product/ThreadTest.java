package com.example.kedamall.product;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest {

    static ExecutorService threadPool = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main方法开始");
        CompletableFuture<Long> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程开始："+Thread.currentThread().getName());
            return 10L;
            }, threadPool);
        Long aLong = future01.get();
        CompletableFuture<Long> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程开始："+Thread.currentThread().getName());
            return 10L;
        }, threadPool);

        future01.runAfterBothAsync(future02,()->{
            System.out.println("任务三开始");
        },threadPool);
    }
}
