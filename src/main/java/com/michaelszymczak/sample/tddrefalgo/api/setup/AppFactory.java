package com.michaelszymczak.sample.tddrefalgo.api.setup;

import com.michaelszymczak.sample.tddrefalgo.api.io.AppIO;

public class AppFactory {
    public static AppIO createApp(AppFactoryRegistry appFactoryRegistry) {
        return new MultiProtocolApp(appFactoryRegistry);
    }
}
