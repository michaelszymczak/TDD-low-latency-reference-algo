package com.michaelszymczak.sample.tddrefalgo.framework.api.setup;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;

public class AppFactory {
    public static AppIO createApp(AppFactoryRegistry appFactoryRegistry) {
        return new MultiProtocolApp(appFactoryRegistry);
    }
}
