package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

class Pool<T> {

    private final Deque<T> pool = new ArrayDeque<>();
    private final Supplier<T> itemSupplier;

    Pool(final Supplier<T> itemSupplier) {
        this.itemSupplier = itemSupplier;
    }

    void returnToPool(T evictedElement) {
        pool.addFirst(evictedElement);
    }

    T get() {
        T pooled = pool.pollFirst();
        if (pooled == null) {
            return itemSupplier.get();
        } else {
            return pooled;
        }
    }
}
