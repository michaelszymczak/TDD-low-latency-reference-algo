package com.michaelszymczak.sample.tddrefalgo.apps.barapp;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactory;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactoryRegistry;
import org.agrona.DirectBuffer;

import java.util.Collections;

public class BarApp implements AppIO {

    private final AppIO app;

    public BarApp() {
        app = AppFactory.createApp(new AppFactoryRegistry(Collections.emptyList()));
    }

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        return app.onInput(input, offset, length);
    }

    @Override
    public Output output() {
        return app.output();
    }
}
