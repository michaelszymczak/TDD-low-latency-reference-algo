package com.michaelszymczak.sample.tddrefalgo.other;

import org.agrona.collections.MutableLong;
import org.agrona.collections.Object2ObjectHashMap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class LowLatencyCoalescingQueue<T> implements CoalescingQueue<T> {

    private static final ThreadLocal<MutableLong> ALLOCATIONS = ThreadLocal.withInitial(MutableLong::new);

    private final Deque<Key> keys = recordAllocation(new ArrayDeque<>());
    private final Map<Key, WrappedElement<T>> elementByKey = recordAllocation(new Object2ObjectHashMap<>());
    private final Key keyPlaceholder = recordAllocation(new Key(""));
    private final Deque<WrappedElement<T>> wrappedElementsPool = recordAllocation(new ArrayDeque<>());

    private static <A> A recordAllocation(A newlyCreatedObject) {
        MutableLong count = ALLOCATIONS.get();
        count.set(count.get() + 1);
        return newlyCreatedObject;
    }

    public long allocations() {
        return ALLOCATIONS.get().get();
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
    public void add(CharSequence key, T element) {
        keyPlaceholder.set(key);
        WrappedElement<T> existingWrappedElement = elementByKey.get(keyPlaceholder);
        if (existingWrappedElement == null) {
            WrappedElement<T> wrappedElement = newWrappedElement(key, element);
            keys.addLast(wrappedElement.key);
            elementByKey.put(wrappedElement.key, wrappedElement);
        } else {
            existingWrappedElement.setElement(element);
        }
    }

    private WrappedElement<T> newWrappedElement(CharSequence key, T element) {
        WrappedElement<T> pooled = wrappedElementsPool.pollFirst();
        if (pooled == null) {
            return recordAllocation(new WrappedElement<>(key, element));
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

    static class WrappedElement<T> {
        final Key key;
        T element;

        WrappedElement(CharSequence key, T element) {
            this.key = recordAllocation(new Key(key));
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

    static class Key {
        final StringBuilder k = recordAllocation(new StringBuilder());

        Key(CharSequence k) {
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
