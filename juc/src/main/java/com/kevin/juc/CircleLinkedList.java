package com.kevin.juc;

import java.io.Serializable;

/**
 * @ClassName CircleLinkedList
 * @Description
 * @Author panhaicheng@koolearn-inc.com
 * @Date 2023/3/1 11:33 上午
 */
public class CircleLinkedList implements Serializable {

    private Node first = null;

    public Node getFirst() {
        return first;
    }

    /**
     * 添加节点
     *
     * @param node
     * @return
     */
    public void add(Node node) {
        if (first == null) {
            first = node;
            first.setNext(first);
            return;
        }
        Node temp = first;
        while (true) {
            if (temp.getNext() == null || temp.getNext() == first) {
                temp.setNext(node);
                node.setNext(first);
                break;
            }
            temp = temp.getNext();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CircleLinkedList:[");
        if(this.first == null){
            return sb.append("]").toString();
        }
        Node node = this.first;
        do{
            sb.append(node.getData()).append(",");
            node = node.getNext();
        }while (node.getData()!=first.getData());
        return sb.append("]").toString();
    }
}
