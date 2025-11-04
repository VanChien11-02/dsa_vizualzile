package org.cuoi_ki.bai1;

import java.util.ArrayList;
import java.util.List;

public class MyStack<E> {
    private Node<E> top;
    private int size;

    private static class Node<E> {
        E data;
        Node<E> next;
        Node(E data) { this.data = data; }
    }

    public MyStack() {
        top = null;
        size = 0;
    }

    public void push(E item) {
        Node<E> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
        size++;
    }

    public E pop() {
        if (isEmpty()) throw new RuntimeException("Stack rỗng!");
        E data = top.data;
        top = top.next;
        size--;
        return data;
    }

    public E peek() {
        if (isEmpty()) throw new RuntimeException("Stack rỗng!");
        return top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        top = null;
        size = 0;
    }

    public List<E> toList() {
        List<E> list = new ArrayList<>();
        Node<E> current = top;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        Node<E> current = top;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) sb.append(", ");
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}