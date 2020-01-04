package com.michaelszymczak.sample.tddrefalgo.other;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class LowLatencyCoalescingQueue<T> implements CoalescingQueue<T> {

    private final Deque<Key> keys = new ArrayDeque<>();
    private final Map<Key, WrappedElement<T>> elementByKey = new HashMap<>();
    private final Key keyPlaceholder = new Key("");

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public T poll() {
        Key key = keys.pollFirst();
        return key == null ? null : elementByKey.remove(key).element;
    }

    @Override
    public void add(CharSequence key, T element) {
        keyPlaceholder.set(key);
        WrappedElement<T> existingWrappedElement = elementByKey.get(keyPlaceholder);
        if (existingWrappedElement == null) {
            WrappedElement<T> wrappedElement = new WrappedElement<>(new Key(key), element);
            keys.addLast(wrappedElement.key);
            elementByKey.put(wrappedElement.key, wrappedElement);
        }
        else {
            existingWrappedElement.setElement(element);
        }

    }

    @Override
    public String toString() {
        return "LowLatencyCoalescingQueue{" +
                "size=" + size() +
                '}';
    }

    static class WrappedElement<T> {
        Key key;
        T element;

        WrappedElement(Key key, T element) {
            this.key = key;
            this.element = element;
        }


        void setElement(T element) {
            this.element = element;
        }
    }

    static class Key {
        final StringBuilder k = new StringBuilder();

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
