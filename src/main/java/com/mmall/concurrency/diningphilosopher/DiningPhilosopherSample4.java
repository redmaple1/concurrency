package com.mmall.concurrency.diningphilosopher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 哲学家就餐 - 使用条件变量condition,对cpu的占用少，但是速度并不是很快
 *
 * @author renxiaoya
 * @date 2021-06-27
 **/
public class DiningPhilosopherSample4 {

    Phi[] phis = new Phi[5];
    volatile int[] forks = new int[5];
    Condition[] waitForks = new Condition[5];
    Lock lock = new ReentrantLock();

    public DiningPhilosopherSample4(){
        for (int i = 0; i < 5; i++) {
            this.phis[i] = new Phi(i + 1);
            this.forks[i] = 0;
            waitForks[i] = lock.newCondition();
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
                    while (!(this.takeLeft(forks))){
                        waitForks[this.left()].await();
                    }
                    while (!(this.takeRight(forks))){
                        waitForks[this.right()].await();
                    }
                    lock.unlock();

                    this.eating();
                    lock.lockInterruptibly();
                    this.putLeft(forks);
                    this.putRight(forks);
                    waitForks[this.left()].signalAll();
                    waitForks[this.right()].signalAll();
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
        DiningPhilosopherSample4 solver = new DiningPhilosopherSample4();
        solver.run();
    }
}
