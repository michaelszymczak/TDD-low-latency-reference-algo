package com.michaelszymczak.sample.tddrefalgo.api.setup;

import com.michaelszymczak.sample.tddrefalgo.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.api.io.Output;
import com.michaelszymczak.sample.tddrefalgo.encoding.*;
import org.agrona.DirectBuffer;

import java.util.ArrayList;
import java.util.List;

class MultiProtocolApp implements AppIO {

    private final LengthBasedMessageEncoding.Decoder decoder;
    private final DecodedAppMessageConsumer consumer;
    private final List<RegisteredApp<?, ?>> registeredApps = new ArrayList<>();
    private final AppPublisher appPublisher;

    MultiProtocolApp(AppFactoryRegistry appFactoryRegistry) {
        this.appPublisher = new AppPublisher();
        appFactoryRegistry.getAppFactories().stream().map(factory -> createApp(appPublisher, factory)).forEach(registeredApps::add);
        this.decoder = new LengthBasedMessageEncoding.Decoder();
        this.consumer = this::onMessage;
    }

    private static <M> void decodeAndHandle(short payloadSchemaId, DirectBuffer buffer, int offset, int length, RegisteredApp<?, M> app) {
        if (payloadSchemaId == app.getProtocolSchemaId()) {
            app.getProtocolDecoder().wrap(buffer, offset, length).decode(app.getDecodedMessageListener());
        }
    }

    private static <D extends ProtocolDecoder<D, L>, E extends ProtocolEncoder<E, M>, L, M> RegisteredApp<D, L> createApp(
            AppPublisher appPublisher,
            RegisteredAppFactory<D, E, L, M> factory
    ) {
        LengthEncodingPublisher<M> lengthEncodingPublisher = new LengthEncodingPublisher<>(
                factory.getProtocolEncoder(),
                appPublisher,
                new LengthBasedMessageEncoding.Encoder());
        return new RegisteredApp<>(
                factory.getProtocolSchema(),
                factory.getProtocolDecoder(),
                factory.getAppFactory().apply(lengthEncodingPublisher)
        );
    }

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        decoder.wrap(input, offset, length).decode(consumer);
        return offset + length;
    }

    @Override
    public Output output() {
        return appPublisher;
    }

    private void onMessage(short payloadSchemaId, DirectBuffer buffer, int offset, int length) {
        for (int i = 0, size = registeredApps.size(); i < size; i++) {
            decodeAndHandle(payloadSchemaId, buffer, offset, length, registeredApps.get(i));
        }
    }

}
