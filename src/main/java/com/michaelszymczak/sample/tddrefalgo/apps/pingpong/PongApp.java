package com.michaelszymczak.sample.tddrefalgo.apps.pingpong;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactory;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactoryRegistry;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.RegisteredAppFactory;
import com.michaelszymczak.sample.tddrefalgo.protocols.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.plaintext.PlainTextListener;
import com.michaelszymczak.sample.tddrefalgo.protocols.time.Time;
import com.michaelszymczak.sample.tddrefalgo.protocols.time.TimeEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.time.TimeMessageListener;
import org.agrona.DirectBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PongApp implements AppIO {

    private static final PayloadSchema PLAIN_TEXT = new PayloadSchema.KnownPayloadSchema((short) 1);
    private static final PayloadSchema TIME = new PayloadSchema.KnownPayloadSchema((short) 2);

    private final AppIO app;
    private final PongModule pongModule = new PongModule();

    public PongApp() {
        app = AppFactory.createApp(new AppFactoryRegistry(1024, Arrays.asList(
                new RegisteredAppFactory<>(
                        PLAIN_TEXT,
                        new PlainTextEncoding.Decoder(),
                        new PlainTextEncoding.Encoder(PLAIN_TEXT),
                        publisher -> pongModule
                ),
                new RegisteredAppFactory<>(
                        TIME,
                        new TimeEncoding.Decoder(),
                        new TimeEncoding.Encoder(TIME),
                        publisher -> pongModule
                )
        )));
    }

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        return app.onInput(input, offset, length);
    }

    @Override
    public Output output() {
        return app.output();
    }

    public List<String> received() {
        return Collections.unmodifiableList(pongModule.received);
    }

    public int heartbeatCount() {
        return pongModule.timeMessagesCount;
    }

    static class PongModule implements PlainTextListener, TimeMessageListener {

        List<String> received = new ArrayList<>();
        int timeMessagesCount = 0;

        @Override
        public void onMessage(String message) {
            received.add(message);
        }

        @Override
        public void onMessage(Time message) {
            timeMessagesCount++;
        }
    }
}
