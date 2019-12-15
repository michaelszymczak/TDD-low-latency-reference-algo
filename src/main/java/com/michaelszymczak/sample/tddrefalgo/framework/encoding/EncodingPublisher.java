package com.michaelszymczak.sample.tddrefalgo.framework.encoding;

public interface EncodingPublisher<M> {

    void publish(M message);
}
