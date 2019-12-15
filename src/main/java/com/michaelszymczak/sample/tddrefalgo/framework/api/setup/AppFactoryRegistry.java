package com.michaelszymczak.sample.tddrefalgo.framework.api.setup;

import java.util.ArrayList;
import java.util.List;

public class AppFactoryRegistry {

    private final List<RegisteredAppFactory<?, ?, ?, ?>> appFactories = new ArrayList<>();

    public AppFactoryRegistry(List<RegisteredAppFactory<?, ?, ?, ?>> appFactories) {
        this.appFactories.addAll(appFactories);
    }

    List<RegisteredAppFactory<?, ?, ?, ?>> getAppFactories() {
        return appFactories;
    }
}
