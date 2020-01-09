package com.michaelszymczak.sample.tddrefalgo.apps.middleman.support;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.Tier;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableQuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.QuotePricingMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Exposure {

    private final List<ImmutableQuotePricingMessage> quotes = new ArrayList<>();

    List<ImmutableQuotePricingMessage> liveQuotes() {
        return quotes.stream().sorted().collect(Collectors.toList());
    }

    public Exposure onPricingMessage(PricingMessage pricingMessage) {
        switch (pricingMessage.type()) {

            case HEARTBEAT:
            case ACK:
                break;
            case QUOTE:
                QuotePricingMessage quote = (QuotePricingMessage) pricingMessage;
                validate(quote);
                quotes.removeIf(q ->
                        q.isin().equals(quote.isin().toString()) &&
                                (Tier.isForCancel(quote.priceTier()) || quote.priceTier() == q.priceTier())
                );
                if (!Tier.isForCancel(quote.priceTier())) {
                    quotes.add(new ImmutableQuotePricingMessage(quote));
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return this;
    }

    private void validate(QuotePricingMessage msg) {
        if (msg.isin().length() == 0 ||
                (!Tier.isValidForQuote(msg.priceTier()) && !Tier.isForCancel(msg.priceTier())) ||
                (!Tier.isForCancel(msg.priceTier()) && msg.askPrice() == 0 && msg.bidPrice() == 0)) {
            throw new IllegalArgumentException("Invalid quote update");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exposure exposure = (Exposure) o;
        return Objects.equals(liveQuotes(), exposure.liveQuotes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(liveQuotes());
    }

    @Override
    public String toString() {
        return liveQuotes().toString();
    }
}
