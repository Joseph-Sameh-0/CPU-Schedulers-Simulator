package org.example.FCAI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomQueue<T extends Comparable<T>> {
    private final List<T> list;

    public CustomQueue() {
        this.list = Collections.synchronizedList(new ArrayList<>());
    }

    // Add an element to the list
    public void add(T element) {
        list.add(element);
    }

    // Get the "first in" element
    public T getFirstIn() {
        synchronized (list) {
            return list.isEmpty() ? null : list.get(0);
        }
    }

    // Get the smallest element
    public T getSmallest() {
        synchronized (list) {
            if (list.isEmpty()) {
                return null;
            }
            T smallest = list.get(0);
            for (T element : list) {
                if (element.compareTo(smallest) < 0) {
                    smallest = element;
                }
            }
            return smallest;
        }
    }

    // Remove the "first in" element
    public T removeFirstIn() {
        synchronized (list) {
            return list.isEmpty() ? null : list.remove(0);
        }
    }

    // Remove the smallest element
    public T removeSmallest() {
        synchronized (list) {
            if (list.isEmpty()) {
                return null;
            }
            T smallest = getSmallest();
            list.remove(smallest);
            return smallest;
        }
    }

    // Check if the queue is empty
    public boolean isEmpty() {
        synchronized (list) {
            return list.isEmpty();
        }
    }
}
