package com.michaelszymczak.sample.tddrefalgo.framework.api.setup;

import java.util.ArrayList;
import java.util.List;

public class AppFactoryRegistry {

    private final List<RegisteredAppFactory<?, ?, ?, ?>> appFactories = new ArrayList<>();
    private final int publisherCapacity;

    public AppFactoryRegistry(final int publisherCapacity, List<RegisteredAppFactory<?, ?, ?, ?>> appFactories) {
        this.appFactories.addAll(appFactories);
        this.publisherCapacity = publisherCapacity;
    }

    List<RegisteredAppFactory<?, ?, ?, ?>> getAppFactories() {
        return appFactories;
    }

    int getPublisherBufferCapacity() {
        return publisherCapacity;
    }


}
