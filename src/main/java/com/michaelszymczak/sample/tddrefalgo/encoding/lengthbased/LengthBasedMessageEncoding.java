package com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.Message;
import com.michaelszymczak.sample.tddrefalgo.encoding.DecodedAppMessageConsumer;
import com.michaelszymczak.sample.tddrefalgo.encoding.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.encoding.ProtocolEncoder;
import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.agrona.BitUtil.SIZE_OF_INT;

public class LengthBasedMessageEncoding {

    private static final int HEADER_SIZE = SIZE_OF_INT + BitUtil.SIZE_OF_SHORT;

    public static class Encoder {

        private final Map<Class<?>, ProtocolEncoder<?, ?>> encoderByProtocol = new HashMap<>();
        private MutableDirectBuffer buffer;
        private int offset;

        public Encoder(List<ProtocolEncoder<?, ?>> protocolEncoders) {
            protocolEncoders.forEach(this::register);
        }

        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public <M> int encode(Message<M> message) {
            final ProtocolEncoder<? extends ProtocolEncoder<?, M>, M> protocolEncoder = encoderFor(message);
            int position = protocolEncoder.wrap(buffer, offset + HEADER_SIZE).encode(message.payload());
            buffer.putInt(offset, position - (offset + HEADER_SIZE));
            buffer.putShort(offset + SIZE_OF_INT, protocolEncoder.payloadSchema().id());
            return position;

        }

        private void register(ProtocolEncoder encoder) {
            encoderByProtocol.put(encoder.messageType(), encoder);
        }

        @SuppressWarnings("unchecked")
        private <M> ProtocolEncoder<? extends ProtocolEncoder<?, M>, M> encoderFor(Message<M> message) {
            ProtocolEncoder<? extends ProtocolEncoder<?, M>, M> protocolEncoder = (ProtocolEncoder<? extends ProtocolEncoder<?, M>, M>) encoderByProtocol.get(message.payloadType());
            if (protocolEncoder == null) {
                throw new IllegalArgumentException();
            }
            return protocolEncoder;
        }
    }

    public interface PayloadSchemaById {
        PayloadSchema get(int schemaId);
    }

    public static class Decoder {

        private final PayloadSchemaById payloadSchemaById;
        private DirectBuffer buffer;
        private int offset;

        public Decoder(PayloadSchemaById payloadSchemaById) {
            this.payloadSchemaById = payloadSchemaById;
        }

        public Decoder wrap(DirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public int decode(final int length, final DecodedAppMessageConsumer consumer) {
            if (length - HEADER_SIZE <= 0) {
                return offset;
            }
            int payloadLength = buffer.getInt(offset);
            short schemaId = buffer.getShort(offset + SIZE_OF_INT);
            consumer.onMessage(schemaId, buffer, offset + HEADER_SIZE, payloadLength);

            return offset + length;
        }
    }
}
