package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.time.TimeEncoding;

import java.util.Arrays;

public class Setup {

    public static LengthBasedMessageEncoding.Encoder encoder() {
        return new LengthBasedMessageEncoding.Encoder(Arrays.asList(
                new PlainTextEncoding.Encoder(),
                new PricingProtocolEncoding.Encoder(),
                new TimeEncoding.Encoder()
        ));
    }
}
