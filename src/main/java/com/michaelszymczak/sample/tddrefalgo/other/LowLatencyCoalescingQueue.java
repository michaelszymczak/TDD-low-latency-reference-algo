package com.michaelszymczak.sample.tddrefalgo.other;

public class LowLatencyCoalescingQueue<T> implements CoalescingQueue<T> {

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T poll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(CharSequence key, T element) {
        throw new UnsupportedOperationException();
    }
}
