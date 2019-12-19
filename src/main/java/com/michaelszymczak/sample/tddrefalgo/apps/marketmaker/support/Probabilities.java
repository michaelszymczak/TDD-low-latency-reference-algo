package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support;

public class Probabilities {
    public final AckProbability ackProbability;
    public final QuoteProbability quoteProbability;

    public Probabilities(
            AckProbability ackProbability,
            QuoteProbability quoteProbability) {
        this.ackProbability = ackProbability;
        this.quoteProbability = quoteProbability;
    }

    public Probabilities(
            AckProbability ackProbability) {
        this(ackProbability, new QuoteProbability(10, 0, 0, 0));
    }

    public Probabilities(
            QuoteProbability quoteProbability) {
        this(new AckProbability(0), quoteProbability);
    }

    public static class AckProbability {
        public final int percentageProbability;

        public AckProbability(int percentageProbability) {
            this.percentageProbability = percentageProbability;
        }
    }

    public static class QuoteProbability {
        public final int distinctInstruments;
        public final int percentageProbability;
        public final int noPriceProbability;
        public final int noTierProbability;

        public QuoteProbability(int distinctInstruments, int percentageProbability, int noPriceProbability, int noTierProbability) {
            this.distinctInstruments = distinctInstruments;
            this.percentageProbability = percentageProbability;
            this.noPriceProbability = noPriceProbability;
            this.noTierProbability = noTierProbability;
        }
    }
}
