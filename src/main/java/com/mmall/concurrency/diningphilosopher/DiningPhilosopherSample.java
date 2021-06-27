package com.mmall.concurrency.diningphilosopher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 哲学家就餐 - 全部锁住
 *
 * @author renxiaoya
 * @date 2021-06-27
 **/
public class DiningPhilosopherSample {

    Phi[] phis = new Phi[5];
    volatile int[] forks = new int[5];
    Lock lock = new ReentrantLock();

    public DiningPhilosopherSample(){
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
                    this.takeLeft(forks);
                    this.takeRight(forks);
                    this.eating();
                    this.putLeft(forks);
                    this.putRight(forks);
                    this.finished();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    lock.unlock();
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
        DiningPhilosopherSample solver = new DiningPhilosopherSample();
        solver.run();
    }
}
