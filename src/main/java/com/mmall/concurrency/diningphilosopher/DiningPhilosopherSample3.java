package com.mmall.concurrency.diningphilosopher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 哲学家就餐 - 减小锁粒度,添加check,提前释放lock，性能好一些
 *
 * @author renxiaoya
 * @date 2021-06-27
 **/
public class DiningPhilosopherSample3 {

    Phi[] phis = new Phi[5];
    volatile int[] forks = new int[5];
    Lock lock = new ReentrantLock();

    public DiningPhilosopherSample3(){
        for (int i = 0; i < 5; i++) {
            this.phis[i] = new Phi(i + 1);
            this.forks[i] = 0;
        }
    }

    class Phi extends Philosopher {

        public Phi(int id) {
            super(id);
        }

        @Override
        protected synchronized boolean takeLeft(int[] forks) {
            return super.takeLeft(forks);
        }

        @Override
        protected synchronized boolean takeRight(int[] forks) {
            return super.takeRight(forks);
        }

        @Override
        public void run() {
            while (true){
                try {
                    this.thinking();
                    lock.lockInterruptibly();
                    if (!(checkLeft(forks) && checkRight(forks))){
                        // 这里是大坑！ 不要忘记
                        lock.unlock();
                        continue;
                    }
                    this.takeLeft(forks);
                    this.takeRight(forks);
                    lock.unlock();

                    this.eating();
                    lock.lockInterruptibly();
                    this.putLeft(forks);
                    this.putRight(forks);
                    lock.unlock();
                    this.finished();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                }

            }
        }

    }

    public void run(){
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            pool.submit(this.phis[i]);
        }
    }

    public static void main(String[] args) {
        DiningPhilosopherSample3 solver = new DiningPhilosopherSample3();
        solver.run();
    }
}
