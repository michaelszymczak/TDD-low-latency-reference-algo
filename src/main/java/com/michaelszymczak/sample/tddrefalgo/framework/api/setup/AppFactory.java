package com.michaelszymczak.sample.tddrefalgo.framework.api.setup;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;

public class AppFactory {
    public static AppIO createApp(AppFactoryRegistry appFactoryRegistry) {
        return createApp(appFactoryRegistry, false);
    }

    public static AppIO createApp(AppFactoryRegistry appFactoryRegistry, final boolean canReturnEarly) {
        return new MultiProtocolApp(appFactoryRegistry);
    }
}
