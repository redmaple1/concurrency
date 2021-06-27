package com.mmall.concurrency.diningphilosopher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 哲学家就餐 - 阻塞队列版本
 * @author renxiaoya
 * @date 2021-06-27
 **/
public class DiningPhilosopherBlockingQueue implements Runnable{

    Philosopher[] phis;
    volatile int[] forks;
    LinkedBlockingQueue<Philosopher> workingQueue;
    LinkedBlockingQueue<Philosopher> managerQueue;

    public DiningPhilosopherBlockingQueue(){
        phis = new Philosopher[5];
        forks = new int[5];
        workingQueue = new LinkedBlockingQueue<>();
        managerQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < 5; i++) {
            phis[i] = new Philosopher(i + 1);
            workingQueue.offer(phis[i]);
        }

    }

    class Worker implements Runnable{

        @Override
        public void run() {
            while (true){
                try {
                    Philosopher phi = workingQueue.take();
                    if (phi.status == StatusEnum.HUNGRY){
                        phi.eating();
                        phi.putLeft(forks);
                        phi.putRight(forks);
                        phi.finished();
                        workingQueue.offer(phi);
                    }else {
                        phi.thinking();
                        managerQueue.offer(phi);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ContentionManager implements Runnable{

        @Override
        public void run() {
            while (true){
                try {
                    Philosopher phi = managerQueue.take();
                    if (phi.checkLeft(forks) && phi.checkRight(forks)){
                        // 拿起叉子
                        phi.takeLeft(forks);
                        phi.takeRight(forks);
                        workingQueue.offer(phi);
                    }else {
                        managerQueue.offer(phi);
                    }
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
        DiningPhilosopherBlockingQueue solver = new DiningPhilosopherBlockingQueue();
        solver.run();
    }
}
