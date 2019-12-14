package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.api.App;
import com.michaelszymczak.sample.tddrefalgo.api.Output;
import com.michaelszymczak.sample.tddrefalgo.encoding.AppPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.DecodedAppMessageConsumer;
import com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased.LengthBasedMessageEncoding;
import org.agrona.DirectBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EncodingApp implements App {

    private final LengthBasedMessageEncoding.Decoder decoder;
    private final DecodedAppMessageConsumer consumer;
    private final List<RegisteredApp<?, ?>> registeredApps = new ArrayList<>();
    private final AppPublisher appPublisher;

    EncodingApp(List<Function<AppPublisher, RegisteredApp<?, ?>>> appPublisherListFunction) {
        this.appPublisher = new AppPublisher();
        this.decoder = new LengthBasedMessageEncoding.Decoder();
        appPublisherListFunction.stream().map(factory -> factory.apply(appPublisher)).forEach(registeredApps::add);
        this.consumer = this::onMessage;
    }


    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        decoder.wrap(input, offset).decode(length, consumer);
        return offset + length;
    }

    private static <M> void decode(short payloadSchemaId, DirectBuffer buffer, int offset, int length, RegisteredApp<?, M> registeredPricingApp) {
        if (payloadSchemaId == registeredPricingApp.getProtocolSchemaId()) {
            registeredPricingApp.getProtocolDecoder().wrap(buffer, offset, length).decode(registeredPricingApp.getDecodedMessageListener());
        }
    }


    private void onMessage(short payloadSchemaId, DirectBuffer buffer, int offset, int length) {
        for (int i = 0, size = registeredApps.size(); i < size; i++) {
            decode(payloadSchemaId, buffer, offset, length, registeredApps.get(i));
        }
    }

    @Override
    public Output output() {
        return appPublisher;
    }

}
