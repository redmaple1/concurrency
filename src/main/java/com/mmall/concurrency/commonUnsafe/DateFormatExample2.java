package com.mmall.concurrency.commonUnsafe;

import com.mmall.concurrency.annotations.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 这样是错误的！！！
 * SimpleDateFormat 在多线程下有问题。如何解决？
 * 利用线程封闭，将SimpleDateFormat定义在局部中
 */
@Slf4j
@NotThreadSafe
public class DateFormatExample2 {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    //线程总数
    private final static int clientTotal = 5000;

    //并发总数
    private final static int threadTotal = 200;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0;i < clientTotal; i++){
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    format();
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
    }

    private static void format(){
        try {
            simpleDateFormat.parse("20180403");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
