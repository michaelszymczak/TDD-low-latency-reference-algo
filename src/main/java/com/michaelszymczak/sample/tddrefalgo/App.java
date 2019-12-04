package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.Heartbeat;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.Quote;
import com.michaelszymczak.sample.tddrefalgo.encoding.MessageEncoding;
import org.agrona.DirectBuffer;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.MutableDirectBuffer;

public class App {

    private final MutableDirectBuffer out = new ExpandableDirectByteBuffer();
    private final MessageEncoding.Decoder decoder = new MessageEncoding.Decoder();
    private final MessageEncoding.Encoder encoder = new MessageEncoding.Encoder();
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

    public long outputOffset() {
        return 0;
    }

    public int outputPosition() {
        return outputWrittenPosition;
    }

    private static class MyDecodedMessageConsumer implements MessageEncoding.DecodedMessageConsumer {

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
