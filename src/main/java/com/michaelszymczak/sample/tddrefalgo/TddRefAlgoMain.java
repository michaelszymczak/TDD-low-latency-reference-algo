package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableQuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.MutableQuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.QuoteEncoding;
import org.agrona.ExpandableDirectByteBuffer;

public class TddRefAlgoMain {

    public static void main(String[] args) {
        System.out.println(new TddRefAlgoMain().foo());
    }

    public String foo() {
        ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer();
        MutableQuotePricingMessage quote = new MutableQuotePricingMessage();
        new QuoteEncoding.Encoder()
                .wrap(buffer, 50)
                .encode(new ImmutableQuotePricingMessage("GB00BD0PCK97", 2, 100_98, 100_95));
        new QuoteEncoding.Decoder()
                .wrap(buffer, 50)
                .decode(quote);
        return new ImmutableQuotePricingMessage(quote).toString();
    }

}
