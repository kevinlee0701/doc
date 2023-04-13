package com.kevin.juc;

import java.util.Arrays;

/**
 * 类 描 述：冒泡排序
 * 创建时间：2023/4/13 18:43
 * 创 建 人：lifeng
 */
public class BubbleSort {
    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // 交换相邻元素的位置
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = {5, 2, 8, 3, 1};
        bubbleSort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
