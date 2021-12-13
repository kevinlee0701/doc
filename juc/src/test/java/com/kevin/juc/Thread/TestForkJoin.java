package com.kevin.juc.Thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

@Slf4j
public class TestForkJoin {

    static Random random = new Random(0);

    static long random() {
        return random.nextInt(10000);
    }

    @Test
    public void forkJoin(){
        // 创建2000个随机数组成的数组:

        long[] array = new long[20];
        long expectedSum = 0;
        for (int i = 0; i < array.length; i++) {
            array[i] = random();
        }
        long start = System.currentTimeMillis();
        for (long param : array) {
            expectedSum+=param;
        }
        long end =System.currentTimeMillis();
//        log.info("array is {}",array);


        // fork/join:
        ForkJoinTask<Long> task = new SumTask(array, 0, array.length);
        long startTime = System.currentTimeMillis();
        Long result = ForkJoinPool.commonPool().invoke(task);
        long endTime = System.currentTimeMillis();
        log.info("当前线程池大小为：{}",ForkJoinPool.commonPool().getParallelism());
        System.out.println("Fork/join sum: " + result + " in " + (endTime - startTime) + " ms.");
        System.out.println("Expected sum: " + expectedSum +",in "+(end-start) +" ms");
    }

    class SumTask extends RecursiveTask<Long> {
        static final int THRESHOLD = 100;
        long[] array;
        int start;
        int end;

        SumTask(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                // 如果任务足够小,直接计算:
                long sum = 0;
                for (int i = start; i < end; i++) {
                     long p = this.array[i];
                    sum += p;
                    // 故意放慢计算速度:
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                    }
                }
                return sum;
            }
            // 任务太大,一分为二:
            int middle = (end + start) / 2;
            System.out.println(String.format("split %d~%d ==> %d~%d, %d~%d", start, end, start, middle, middle, end)+",Thread name "+Thread.currentThread().getName());
            SumTask subtask1 = new SumTask(this.array, start, middle);
            SumTask subtask2 = new SumTask(this.array, middle, end);
            invokeAll(subtask1, subtask2);
            Long subresult1 = subtask1.join();
            Long subresult2 = subtask2.join();
            Long result = subresult1 + subresult2;
            System.out.println("result = " + subresult1 + " + " + subresult2 + " ==> " + result);
            return result;
        }
    }


}
