package com.kevin.juc;

import java.util.LinkedList;

import com.alibaba.fastjson.JSON;
/**
 * 固定长度List
 * 如果List里面的元素个数大于了缓存最大容量，则删除链表的顶端元素
 * @author lixiangjing
 */
public class FixSizeLinkedList<T> extends LinkedList<T> {
    private static final long serialVersionUID = 3292612616231532364L;
    // 定义缓存的容量
    private int capacity;

    public FixSizeLinkedList(int capacity) {
        super();
        this.capacity = capacity;
    }

    @Override
    public void addFirst(T e) {
        if(this.contains(e)){
           super.remove(e);
        }
        // 超过长度，移除最后一个
        if (size() + 1 > capacity) {
            super.removeLast();
        }
        super.addFirst(e);
    }

    @Override
    public boolean add(T e) {
        if(this.contains(e)){
            return true;
        }
        // 超过长度，移除最后一个
        if (size() + 1 > capacity) {
            super.removeFirst();
        }
        return super.add(e);
    }

    public static void main(String[] args) {
        FixSizeLinkedList<String> list = new FixSizeLinkedList<>(3);
        list.addFirst("1");
        list.addFirst("2");
        list.addFirst("3");
        list.addFirst("1");
        list.addFirst("4");
        System.out.println(JSON.toJSONString(list));
    }
}