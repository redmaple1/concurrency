package com.mmall.concurrency.diningphilosopher;

import java.util.concurrent.*;

/**
 * 哲学家就餐 - 阻塞队列版本,使用延迟队列添加错误回滚
 *
 * @author renxiaoya
 * @date 2021-06-27
 **/
public class DiningPhilosopherBlockingQueue2 implements Runnable {

    Philosopher[] phis;
    volatile int[] forks;
    LinkedBlockingQueue<Philosopher> workingQueue;
    LinkedBlockingQueue<Philosopher> managerQueue;
    DelayQueue<DelayInterruptingThread> delayQueue = new DelayQueue<>();

    public DiningPhilosopherBlockingQueue2() {
        phis = new Philosopher[5];
        forks = new int[5];
        workingQueue = new LinkedBlockingQueue<>();
        managerQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < 5; i++) {
            phis[i] = new Philosopher(i + 1);
            workingQueue.offer(phis[i]);
        }

    }

    class Worker implements Runnable {

        @Override
        public void run() {
            while (true) {
                Philosopher phi = null;
                try {
                    phi = workingQueue.take();
                    if (phi.status == StatusEnum.HUNGRY) {
                        DelayInterruptingThread delayItem = new DelayInterruptingThread(Thread.currentThread(), 1000);
                        delayQueue.offer(delayItem);
                        phi.eating();
                        delayItem.commit();
                        phi.putLeft(forks);
                        phi.putRight(forks);
                        phi.finished();
                        workingQueue.offer(phi);
                    } else {
                        phi.thinking();
                        managerQueue.offer(phi);
                    }
                } catch (InterruptedException e) {
                    if (phi != null) {
                        phi.putLeft(forks);
                        phi.putRight(forks);
                        if (phi.status == StatusEnum.EATING) {
                            phi.status = StatusEnum.HUNGRY;
                        }
                        managerQueue.offer(phi);
                    }
                }
            }
        }
    }

    class ContentionManager implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Philosopher phi = managerQueue.take();
                    if (phi.checkLeft(forks) && phi.checkRight(forks)) {
                        // 拿起叉子
                        phi.takeLeft(forks);
                        phi.takeRight(forks);
                        workingQueue.offer(phi);
                    } else {
                        managerQueue.offer(phi);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class DelayInterruptingThread implements Delayed {

        long time;
        Thread current;

        public DelayInterruptingThread(Thread t, long delay) {
            this.current = t;
            this.time = System.currentTimeMillis() + delay;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return time - System.currentTimeMillis();
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (time - ((DelayInterruptingThread) o).time);
        }

        public void rollback() {
            if (current != null) {
                this.current.interrupt();
            }
        }

        public void commit() {
            this.current = null;
        }
    }

    class InterruptingWorker implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    DelayInterruptingThread delay = delayQueue.take();
                    delay.rollback();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(6);
        for (int i = 0; i < 5; i++) {
            pool.submit(new Worker());
        }
        pool.submit(new ContentionManager());
    }

    public static void main(String[] args) {
        DiningPhilosopherBlockingQueue2 solver = new DiningPhilosopherBlockingQueue2();
        solver.run();
    }
}
