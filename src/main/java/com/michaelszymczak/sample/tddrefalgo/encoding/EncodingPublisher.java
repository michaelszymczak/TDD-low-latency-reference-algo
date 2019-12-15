package com.michaelszymczak.sample.tddrefalgo.encoding;

public interface EncodingPublisher<M> {

    void publish(M message);
}
