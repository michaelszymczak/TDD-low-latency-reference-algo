package com.michaelszymczak.sample.tddrefalgo.coalescingqueue;

import org.agrona.collections.MutableInteger;
import org.agrona.collections.Object2ObjectHashMap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class LowLatencyCoalescingQueue<T> implements CoalescingQueue<T> {

    private final AllocationCounter allocationsCounter;
    private final Deque<Key> keys;
    private final Map<Key, WrappedElement<T>> elementByKey;
    private final Key keyPlaceholder;
    private final Deque<WrappedElement<T>> wrappedElementsPool;

    public LowLatencyCoalescingQueue() {
        this.allocationsCounter = new AllocationCounter();
        this.keys = this.allocationsCounter.recordAllocation(new ArrayDeque<>());
        this.elementByKey = this.allocationsCounter.recordAllocation(new Object2ObjectHashMap<>());
        this.keyPlaceholder = this.allocationsCounter.recordAllocation(new Key(allocationsCounter, ""));
        this.wrappedElementsPool = this.allocationsCounter.recordAllocation(new ArrayDeque<>());
    }

    public long allocations() {
        return allocationsCounter.count();
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public T poll() {
        Key key = keys.pollFirst();
        if (key == null) {
            return null;
        } else {
            WrappedElement<T> removedWrappedElement = elementByKey.remove(key);
            T element = removedWrappedElement.element;
            returnToThePool(removedWrappedElement);
            return element;
        }
    }

    @Override
    public void add(CharSequence key, T element, EvictedElementListener<? super T> evictedElementConsumer) {
        keyPlaceholder.set(key);
        WrappedElement<T> existingWrappedElement = elementByKey.get(keyPlaceholder);
        if (existingWrappedElement == null) {
            WrappedElement<T> wrappedElement = newWrappedElement(key, element);
            keys.addLast(wrappedElement.key);
            elementByKey.put(wrappedElement.key, wrappedElement);
        } else {
            evictedElementConsumer.onEvicted(existingWrappedElement.element);
            existingWrappedElement.setElement(element);
        }
    }

    private WrappedElement<T> newWrappedElement(CharSequence key, T element) {
        WrappedElement<T> pooled = wrappedElementsPool.pollFirst();
        if (pooled == null) {
            return allocationsCounter.recordAllocation(new WrappedElement<>(allocationsCounter, key, element));
        } else {
            return pooled.set(key, element);
        }
    }

    private void returnToThePool(WrappedElement<T> wrappedElement) {
        wrappedElement.clear();
        wrappedElementsPool.addFirst(wrappedElement);
    }

    @Override
    public String toString() {
        return "LowLatencyCoalescingQueue{" +
                "size=" + size() +
                '}';
    }

    private static class AllocationCounter {
        private final MutableInteger count = new MutableInteger();

        <A> A recordAllocation(A newlyCreatedObject) {
            count.set(count.get() + 1);
            return newlyCreatedObject;
        }

        int count()
        {
            return count.get();
        }
    }

    private static class WrappedElement<T> {
        final Key key;
        T element;

        WrappedElement(AllocationCounter allocationCounter, CharSequence key, T element) {
            this.key = allocationCounter.recordAllocation(new Key(allocationCounter, key));
            this.element = element;
        }


        public WrappedElement<T> set(CharSequence key, T element) {
            this.key.set(key);
            this.element = element;
            return this;
        }

        void setElement(T element) {
            this.element = element;
        }

        void clear() {
            this.key.set("");
            this.element = null;
        }
    }

    private static class Key {
        final StringBuilder k;

        Key(AllocationCounter allocationsCounter, CharSequence k) {
            this.k = allocationsCounter.recordAllocation(new StringBuilder());
            this.k.setLength(0);
            this.k.append(k);
        }

        public void set(CharSequence k) {
            this.k.setLength(0);
            this.k.append(k);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            if (key.k.length() != this.k.length()) {
                return false;
            }
            for (int i = 0, length = k.length(); i < length; i++) {
                if (key.k.charAt(i) != this.k.charAt(i)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = 1;
            for (int i = 0, length = k.length(); i < length; i++) {
                result = 31 * result + k.charAt(i);
            }
            return result;
        }

        @Override
        public String toString() {
            return k.toString();
        }
    }
}
