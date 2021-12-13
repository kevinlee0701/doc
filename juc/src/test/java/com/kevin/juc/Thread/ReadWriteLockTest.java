package com.kevin.juc.Thread;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写互斥，写写互斥，读读不互斥
 * 把读写操作分别用读锁和写锁来加锁，在读取时，多个线程可以同时获得读锁，这样就大大提高了并发读的执行效率。
 *
 * 使用ReadWriteLock时，适用条件是同一个数据，有大量线程读取，但仅有少数线程修改。
 *
 * 例如，一个论坛的帖子，回复可以看做写入操作，它是不频繁的，但是，浏览可以看做读取操作，是非常频繁的，这种情况就可以使用ReadWriteLock。
 *
 * 使用ReadWriteLock可以提高读取效率：
 *
 * ReadWriteLock只允许一个线程写入；
 *
 * ReadWriteLock允许多个线程在没有写入时同时读取；
 *
 * ReadWriteLock适合读多写少的场景。
 *
 */
public class ReadWriteLockTest {
    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private final Lock rlock = rwlock.readLock();
    private final Lock wlock = rwlock.writeLock();
    private int[] counts = new int[10];

    public void inc(int index) {
        wlock.lock(); // 加写锁
        try {
            counts[index] += 1;
        } finally {
            wlock.unlock(); // 释放写锁
        }
    }

    public int[] get() {
        rlock.lock(); // 加读锁
        try {
            return Arrays.copyOf(counts, counts.length);
        } finally {
            rlock.unlock(); // 释放读锁
        }
    }
}
