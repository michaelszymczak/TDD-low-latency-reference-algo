package com.michaelszymczak.sample.tddrefalgo.protocols.plaintext;

import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.ProtocolDecoder;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.ProtocolEncoder;
import org.agrona.AsciiSequenceView;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public class PlainTextEncoding {

    public static class Encoder implements ProtocolEncoder<PlainTextEncoding.Encoder, String> {
        private final PayloadSchema payloadSchema;
        private MutableDirectBuffer buffer;
        private int offset;

        public Encoder(PayloadSchema payloadSchema) {
            this.payloadSchema = payloadSchema;
        }


        @Override
        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        @Override
        public int encode(String message) {
            buffer.putStringWithoutLengthAscii(offset, message);
            return offset + message.length();
        }

        @Override
        public PayloadSchema payloadSchema() {
            return payloadSchema;
        }

    }

    public static class Decoder implements ProtocolDecoder<Decoder, PlainTextListener> {
        private final AsciiSequenceView asciiSequenceView = new AsciiSequenceView();
        private DirectBuffer buffer;
        private int offset;
        private int length;


        @Override
        public Decoder wrap(DirectBuffer buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
            return this;
        }

        @Override
        public int decode(PlainTextListener decodedMessageListener) {
            String decoded = asciiSequenceView.wrap(buffer, offset, length).toString();
            decodedMessageListener.onMessage(decoded);
            return offset + decoded.length();
        }
    }
}
