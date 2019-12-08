package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.Heartbeat;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.MessageWithPricingProtocol;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.Quote;
import com.michaelszymczak.sample.tddrefalgo.encoding.MessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
import org.agrona.DirectBuffer;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.MutableDirectBuffer;

public class App {

    private final MutableDirectBuffer out = new ExpandableDirectByteBuffer();
    private final MessageEncoding.Decoder decoder = new MessageEncoding.Decoder();
    private final MessageEncoding.Encoder encoder = new MessageEncoding.Encoder();
    private final MessageConsumer messageConsumer = new MessageConsumer();
    private final MessageWithPricingProtocol messageWithPricingProtocol = new MessageWithPricingProtocol();

    private int outputWrittenPosition = 0;


    public int onInput(DirectBuffer input, int offset, int length) {
        decoder.wrap(input, offset).decode(length, messageConsumer.reset());
        if (messageConsumer.pricing().heartbeat != null) {
            this.outputWrittenPosition = encoder
                    .wrap(out, this.outputWrittenPosition)
                    .encode(messageWithPricingProtocol.withPayload(messageConsumer.pricing().heartbeat));
            return offset + length;
        }
        return offset;
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

    private static class MessageConsumer implements MessageEncoding.DecodedMessageConsumer {

        private final PricingProtocolEncoding.Decoder decoder = new PricingProtocolEncoding.Decoder();
        private final PricingDecodedMessageConsumer messageConsumer = new PricingDecodedMessageConsumer();

        @Override
        public void onMessage(PayloadSchema payloadSchema, DirectBuffer buffer, int offset, int length) {
            if (payloadSchema != PayloadSchema.PRICING) {
                throw new UnsupportedOperationException();
            }
            decoder.wrap(buffer, offset).decode(messageConsumer.reset());
        }

        PricingDecodedMessageConsumer pricing() {
            return messageConsumer;
        }

        MessageEncoding.DecodedMessageConsumer reset() {
            messageConsumer.reset();
            return this;
        }
    }

    private static class PricingDecodedMessageConsumer implements PricingProtocolEncoding.DecodedMessageConsumer {

        Heartbeat heartbeat;
        Quote quote;

        PricingDecodedMessageConsumer reset() {
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
