package com.michaelszymczak.sample.tddrefalgo.testsupport;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PricingProtocolDecodedMessageSpy implements PricingProtocolListener {

    private List<PricingMessage> pricingMessages = new ArrayList<>();


    @Override
    public void onMessage(HeartbeatPricingMessage message) {
        pricingMessages.add(new ImmutableHeartbeatPricingMessage(message));
    }

    @Override
    public void onMessage(QuotePricingMessage message) {
        pricingMessages.add(new ImmutableQuotePricingMessage(message));
    }

    @Override
    public void onMessage(AckMessage message) {
        pricingMessages.add(message);
    }

    public List<PricingMessage> receivedMessages() {
        return pricingMessages;
    }

    public <T extends PricingMessage> List<T> receivedMessages(Class<T> type, Predicate<T> predicate) {
        return receivedMessages().stream()
                .filter(msg -> type.isAssignableFrom(msg.getClass()))
                .map(type::cast)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public void clear() {
        pricingMessages.clear();
    }
}
