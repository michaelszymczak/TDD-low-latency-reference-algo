package com.michaelszymczak.sample.tddrefalgo.other;

public interface CoalescingQueue<T> {
    int size();

    T poll();

    void add(CharSequence key, T element);
}
