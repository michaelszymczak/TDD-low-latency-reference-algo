package com.michaelszymczak.sample.tddrefalgo.apps.pingpong;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactory;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactoryRegistry;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.RegisteredAppFactory;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.EncodingPublisher;
import com.michaelszymczak.sample.tddrefalgo.modules.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.modules.plaintext.PlainTextListener;
import com.michaelszymczak.sample.tddrefalgo.modules.time.Time;
import com.michaelszymczak.sample.tddrefalgo.modules.time.TimeEncoding;
import com.michaelszymczak.sample.tddrefalgo.modules.time.TimeMessageListener;

import java.util.Arrays;

public class PingApp {

    private static final PayloadSchema PLAIN_TEXT = new PayloadSchema.KnownPayloadSchema((short) 1);
    private static final PayloadSchema TIME = new PayloadSchema.KnownPayloadSchema((short) 2);
    private final AppIO app;
    private final PingModule pingModule = new PingModule();
    private Time timeMessage = new Time(0);

    public PingApp(final int publisherCapacity) {
        app = AppFactory.createApp(new AppFactoryRegistry(publisherCapacity, Arrays.asList(
                new RegisteredAppFactory<>(
                        PLAIN_TEXT,
                        new PlainTextEncoding.Decoder(),
                        new PlainTextEncoding.Encoder(PLAIN_TEXT),
                        pingModule::withTextPublisher
                ),
                new RegisteredAppFactory<>(
                        TIME,
                        new TimeEncoding.Decoder(),
                        new TimeEncoding.Encoder(TIME),
                        pingModule::withTimePublisher
                )
        )));
    }

    public Output output() {
        return app.output();
    }

    public void ping() {
        pingModule.onMessage("ping");
    }

//    private final List<Long> all = new ArrayList<>();

    public void heartbeat() {
        long timeNanos = System.nanoTime();
//        if (!all.contains(timeNanos))
//        {
//            all.add(timeNanos);
//        }

        pingModule.onMessage(timeMessage.set(timeNanos));
    }

    static class PingModule implements PlainTextListener, TimeMessageListener {

        private EncodingPublisher<String> textPublisher;
        private EncodingPublisher<Time> timePublisher;

        PingModule withTextPublisher(EncodingPublisher<String> publisher) {
            this.textPublisher = publisher;
            return this;
        }

        PingModule withTimePublisher(EncodingPublisher<Time> timePublisher) {
            this.timePublisher = timePublisher;
            return this;
        }

        @Override
        public void onMessage(String message) {
            textPublisher.publish(message);
        }

        @Override
        public void onMessage(Time message) {
            timePublisher.publish(message);
        }
    }
}
