package com.michaelszymczak.sample.tddrefalgo.other;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class ReferenceCoalescingQueue<T> implements CoalescingQueue<T> {

    private final Deque<String> keys = new ArrayDeque<>();
    private final Map<String, T> elementByKey = new HashMap<>();

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public T poll() {
        String key = keys.pollFirst();
        return key == null ? null : elementByKey.remove(key);
    }

    @Override
    public void add(CharSequence key, T element) {
        if (!elementByKey.containsKey(key.toString())) {
            keys.addLast(key.toString());
        }
        elementByKey.put(key.toString(), element);
    }

    @Override
    public String toString() {
        return "ReferenceCoalescingQueue{" +
                "size=" + size() +
                '}';
    }
}
