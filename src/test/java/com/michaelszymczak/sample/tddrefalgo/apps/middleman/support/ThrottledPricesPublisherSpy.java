package com.michaelszymczak.sample.tddrefalgo.apps.middleman.support;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ThrottledPricesPublisherSpy implements ThrottledPricesPublisher {

    private final List<Object> published = new ArrayList<>();

    public static Heartbeat heartbeat(long nanoTime) {
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.nanoTime = nanoTime;
        return heartbeat;
    }

    public static Quote quote(CharSequence isin, int priceTier, long bidPrice, long askPrice) {
        Quote quote = new Quote();
        quote.isin = isin.toString();
        quote.priceTier = priceTier;
        quote.bidPrice = bidPrice;
        quote.askPrice = askPrice;
        return quote;
    }

    public static Cancel cancel(CharSequence isin, int priceTier) {
        Cancel cancel = new Cancel();
        cancel.isin = isin.toString();
        cancel.priceTier = priceTier;
        return cancel;
    }

    public void assertPublishedNothing() {
        assertPublished();
    }

    public void assertPublished(final Object... expected) {
        assertThat(published).usingRecursiveFieldByFieldElementComparator().isEqualTo(Arrays.asList(expected));
    }

    @Override
    public void publishHeartbeat(long nanoTime) {
        published.add(heartbeat(nanoTime));
    }

    @Override
    public void publishCancel(CharSequence isin, int tier) {
        published.add(cancel(isin, tier));
    }

    @Override
    public void publishQuote(CharSequence isin, int tier, long bidPrice, long askPrice) {
        published.add(quote(isin, tier, bidPrice, askPrice));
    }

    public static class Heartbeat {
        long nanoTime;

        @Override
        public String toString() {
            return "Heartbeat{" +
                    "nanoTime=" + nanoTime +
                    '}';
        }
    }

    public static class Quote {
        String isin;
        int priceTier;
        long bidPrice;
        long askPrice;

        @Override
        public String toString() {
            return "Quote{" +
                    "isin='" + isin + '\'' +
                    ", priceTier=" + priceTier +
                    ", bidPrice=" + bidPrice +
                    ", askPrice=" + askPrice +
                    '}';
        }
    }

    public static class Cancel {
        String isin;
        int priceTier;

        @Override
        public String toString() {
            return "Cancel{" +
                    "isin='" + isin + '\'' +
                    ", priceTier=" + priceTier +
                    '}';
        }
    }
}
