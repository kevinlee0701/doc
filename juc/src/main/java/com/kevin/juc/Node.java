package com.kevin.juc;

import java.io.Serializable;

/**
 *  Node
 * @Description
 * @Author panhaicheng@koolearn-inc.com
 * @Date 2023/3/1 11:21 上午
 */
public class Node implements Serializable {

    private int data;
    private Node next;

    public Node(int data, Node next){
        this.data = data;
        this.next = next;
    }

    public Node(int data){
        this.data = data;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
