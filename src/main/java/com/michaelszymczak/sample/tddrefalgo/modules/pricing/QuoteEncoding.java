package com.michaelszymczak.sample.tddrefalgo.modules.pricing;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_INT;
import static org.agrona.BitUtil.SIZE_OF_LONG;

public class QuoteEncoding {

    private static final int ISIN_OFFSET = 0;
    private static final int ISIN_LENGTH = 12;
    private static final int PRICE_TIER_OFFSET = 12;
    private static final int BID_PRICE_OFFSET = 12 + SIZE_OF_INT;
    private static final int ASK_PRICE_OFFSET = 12 + SIZE_OF_INT + SIZE_OF_LONG;
    private static final int TOTAL_LENGTH = ISIN_LENGTH + SIZE_OF_INT + SIZE_OF_LONG + SIZE_OF_LONG;

    public static class Encoder {

        private MutableDirectBuffer buffer;
        private int offset;


        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public int encode(Quote quote) {
            int isinLength = quote.isin().length();
            for (int i = ISIN_OFFSET; i < ISIN_LENGTH && i < isinLength; i++) {
                buffer.putChar(offset + i, quote.isin().charAt(i));
            }
            for (int i = ISIN_OFFSET + isinLength; i < ISIN_LENGTH; i++) {
                buffer.putChar(offset + i, ' ');
            }
            buffer.putInt(offset + PRICE_TIER_OFFSET, quote.priceTier());
            buffer.putLong(offset + BID_PRICE_OFFSET, quote.bidPrice());
            buffer.putLong(offset + ASK_PRICE_OFFSET, quote.askPrice());
            return offset + TOTAL_LENGTH;
        }
    }

    public static class Decoder {

        private static final int ISIN_LENGTH = 12;

        private DirectBuffer buffer;
        private int offset;
        private StringBuilder stringBuilder = new StringBuilder();


        public Decoder wrap(DirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public int decode(MutableQuote result) {
            stringBuilder.setLength(0);
            buffer.getStringWithoutLengthAscii(offset + ISIN_OFFSET, ISIN_LENGTH, stringBuilder);
            result.set(
                    stringBuilder.toString(),
                    buffer.getInt(offset + PRICE_TIER_OFFSET),
                    buffer.getLong(offset + BID_PRICE_OFFSET),
                    buffer.getLong(offset + ASK_PRICE_OFFSET)

            );
            return offset + TOTAL_LENGTH;
        }
    }
}
