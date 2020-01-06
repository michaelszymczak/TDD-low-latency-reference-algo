package com.michaelszymczak.sample.tddrefalgo.coalescingqueue;


public interface CoalescingQueue<T> {

    EvictedElementListener<Object> DROP_EVICTED_ELEMENT = evictedElement -> {

    };

    int size();

    T poll();

    void add(CharSequence key, T element, EvictedElementListener<? super T> evictedElementConsumer);

    interface EvictedElementListener<T> {
        void onEvicted(T evictedElement);
    }
}
