package com.kevinlee;


import java.io.Serializable;
import java.util.LinkedList;

/**
 * 固定长度List[可以放到redis缓存]
 * 如果List里面的元素个数大于了缓存最大容量，则删除链表的顶端元素
 * @author lifeng
 */
public class FixSizeLinkedListCache<T> implements Serializable {
    private static final long serialVersionUID = 3292612616231532364L;
    // 定义缓存的容量
    private Integer capacity;

    private LinkedList<T> linkedList ;

    private Integer test=1000;

    public FixSizeLinkedListCache() {
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LinkedList<T> getLinkedList() {
        return linkedList;
    }

    public void setLinkedList(LinkedList<T> linkedList) {
        this.linkedList = linkedList;
    }

    public FixSizeLinkedListCache(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * 最新的放到最后面
     *
     * @param e
     * @return
     */

    public boolean add(T e) {
        if(linkedList == null){
            linkedList = new LinkedList<>();
        }
        if (linkedList.contains(e)) {
            linkedList.remove(e);
        }
        // 超过长度，移除最后一个
        if (linkedList.size() + 1 > capacity) {
            linkedList.removeFirst();
        }
        return linkedList.add(e);
    }


    @Override
    public String toString() {
        return "FixSizeLinkedList{" +
                "capacity=" + capacity +
                ", linkedList=" + linkedList +
                ", test=" + test +
                '}';
    }
}
