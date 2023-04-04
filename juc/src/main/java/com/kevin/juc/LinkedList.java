package com.kevin.juc;

/**
 * 类 描 述：链表
 * 创建时间：2023/4/4 16:58
 * 创 建 人：lifeng
 */
public class LinkedList {

    // 内部节点类
    private class Node {
        int data;
        Node next;

        public Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;

    public LinkedList() {
        this.head = null;
    }

    // 在链表头添加节点
    public void addAtHead(int data) {
        Node newNode = new Node(data);
        newNode.next = head;
        head = newNode;
    }

    // 在链表尾添加节点
    public void addAtTail(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            return;
        }
        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = newNode;
    }

    // 在指定位置插入节点
    public void addAtIndex(int index, int data) {
        if (index < 0) {
            return;
        }
        if (index == 0) {
            addAtHead(data);
            return;
        }
        Node newNode = new Node(data);
        Node current = head;
        int i = 0;
        while (i < index - 1 && current != null) {
            current = current.next;
            i++;
        }
        if (current == null) {
            return;
        }
        newNode.next = current.next;
        current.next = newNode;
    }

    // 删除指定位置的节点
    public void deleteAtIndex(int index) {
        if (index < 0 || head == null) {
            return;
        }
        if (index == 0) {
            head = head.next;
            return;
        }
        Node current = head;
        int i = 0;
        while (i < index - 1 && current != null) {
            current = current.next;
            i++;
        }
        if (current == null || current.next == null) {
            return;
        }
        current.next = current.next.next;
    }

    // 获取链表的长度
    public int size() {
        int size = 0;
        Node current = head;
        while (current != null) {
            size++;
            current = current.next;
        }
        return size;
    }

    // 打印链表中的所有节点值
    public void print() {
        Node current = head;
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        LinkedList list = new LinkedList();
        list.addAtHead(1);
        list.addAtTail(2);
        list.addAtIndex(1, 3);
        list.print(); // 输出 1 3 2
        list.deleteAtIndex(1);
        list.print(); // 输出 1 2
        System.out.println(list.size()); // 输出 2
    }
}
