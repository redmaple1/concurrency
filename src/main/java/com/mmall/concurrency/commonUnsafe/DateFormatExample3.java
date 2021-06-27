package com.mmall.concurrency.commonUnsafe;

import com.mmall.concurrency.annotations.NotThreadSafe;
import com.mmall.concurrency.annotations.Recommend;
import com.mmall.concurrency.annotations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 推荐使用 joda-time 处理日期时间格式问题
 * 是线程安全的！
 */
@Slf4j
@ThreadSafe
@Recommend
public class DateFormatExample3 {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");

    //线程总数
    private final static int clientTotal = 5000;

    //并发总数
    private final static int threadTotal = 200;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0;i < clientTotal; i++){
            final int count = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    format(count);
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

    private static void format(int i){

        log.info("{},{}",i,DateTime.parse("20180403",dateTimeFormatter).toDate());

    }

}
