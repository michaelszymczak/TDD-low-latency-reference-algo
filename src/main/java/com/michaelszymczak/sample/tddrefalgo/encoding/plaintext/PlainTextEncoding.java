package com.michaelszymczak.sample.tddrefalgo.encoding.plaintext;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.PlainTextListener;
import org.agrona.AsciiSequenceView;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public class PlainTextEncoding {

    public static class Encoder {
        private MutableDirectBuffer buffer;
        private int offset;


        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public int encode(String message) {
            buffer.putStringWithoutLengthAscii(offset, message);
            return offset + message.length();
        }
    }

    public static class Decoder {
        private final AsciiSequenceView asciiSequenceView = new AsciiSequenceView();
        private DirectBuffer buffer;
        private int offset;
        private int length;


        public Decoder wrap(DirectBuffer buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
            return this;
        }

        public int decode(PlainTextListener listener) {
            String decoded = asciiSequenceView.wrap(buffer, offset, length).toString();
            listener.onPlainTextMessage(decoded);
            return offset + decoded.length();
        }
    }
}
