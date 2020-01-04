package com.michaelszymczak.sample.tddrefalgo.other;

public class LowLatencyCoalescingQueue<T> implements CoalescingQueue<T> {

    @Override
    public int size() {
        return 0;
    }

    @Override
    public T poll() {
        return null;
    }

    @Override
    public void add(CharSequence key, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "LowLatencyCoalescingQueue{" +
                "size=" + size() +
                '}';
    }
}
