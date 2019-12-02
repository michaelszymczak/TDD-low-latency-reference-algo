package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.ImmutableQuote;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.MutableQuote;
import com.michaelszymczak.sample.tddrefalgo.encoding.QuoteEncoding;
import org.agrona.ExpandableDirectByteBuffer;

public class TddRefAlgoMain {

    public static void main(String[] args) {
        System.out.println(new TddRefAlgoMain().foo());
    }

    public String foo() {
        ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer();
        MutableQuote quote = new MutableQuote();
        new QuoteEncoding.Encoder()
                .wrap(buffer, 50)
                .encode(new ImmutableQuote("GB00BD0PCK97", 2, 100_98, 100_95));
        new QuoteEncoding.Decoder()
                .wrap(buffer, 50)
                .decode(quote);
        return new ImmutableQuote(quote).toString();
    }

}
