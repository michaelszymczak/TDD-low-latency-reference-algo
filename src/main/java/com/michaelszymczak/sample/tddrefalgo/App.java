package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.Heartbeat;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.Quote;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
import org.agrona.DirectBuffer;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.MutableDirectBuffer;

public class App {

    private final MutableDirectBuffer out = new ExpandableDirectByteBuffer();
    private final PricingProtocolEncoding.Decoder decoder = new PricingProtocolEncoding.Decoder();
    private final PricingProtocolEncoding.Encoder encoder = new PricingProtocolEncoding.Encoder();
    private final MyDecodedMessageConsumer messageConsumer = new MyDecodedMessageConsumer();
    private int outputWrittenPosition = 0;

    public int onInput(DirectBuffer input, int offset, int length) {
        int decode = decoder.wrap(input, offset).decode(messageConsumer.reset());
        if (messageConsumer.heartbeat != null) {
            this.outputWrittenPosition =
                    encoder.wrap(out, this.outputWrittenPosition).encode(messageConsumer.heartbeat);
            return decode;
        }
        return 0;
    }

    public DirectBuffer output() {
        return out;
    }

    public int outputOffset() {
        return 0;
    }

    public int outputPosition() {
        return outputWrittenPosition;
    }

    private static class MyDecodedMessageConsumer implements PricingProtocolEncoding.DecodedMessageConsumer {

        Heartbeat heartbeat;
        Quote quote;

        MyDecodedMessageConsumer reset() {
            heartbeat = null;
            quote = null;
            return this;
        }

        @Override
        public void onHeartbeat(Heartbeat message) {
            this.heartbeat = message;
        }

        @Override
        public void onQuote(Quote message) {
            this.quote = message;
        }
    }
}
