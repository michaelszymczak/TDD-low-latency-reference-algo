package com.michaelszymczak.sample.tddrefalgo.testsupport;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.AckMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.QuotePricingMessage;

import java.util.List;
import java.util.stream.Collectors;

class PricingMessagePrettyPrint {

    static String prettyPrint(String delimiter, final List<PricingMessage> pricingMessages) {
        return pricingMessages.stream().map(msg -> {
            if (msg instanceof QuotePricingMessage) {
                QuotePricingMessage q = (QuotePricingMessage) msg;
                return String.format("Q/%s/%d/%d/%d", q.isin().toString().trim(), q.priceTier(), q.bidPrice(), q.askPrice());
            }
            if (msg instanceof AckMessage) {
                return "A";
            }
            return msg.toString();
        }).collect(Collectors.joining(delimiter));
    }
}
