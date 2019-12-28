package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support;

import static com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities.QuoteProbability.quoteProbability;

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
            QuoteProbability quoteProbability) {
        this.ackProbability = new AckProbability(0);
        this.quoteProbability = quoteProbability;
    }

    public Probabilities(
            AckProbability ackProbability) {
        this(ackProbability, quoteProbability()
                .withPercentageProbability(0)
                .build()
        );
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
        public final int cancellationProbability;

        private QuoteProbability(int percentageProbability, int distinctInstruments, int cancellationProbability) {
            this.distinctInstruments = distinctInstruments;
            this.percentageProbability = percentageProbability;
            this.cancellationProbability = cancellationProbability;
        }

        public static QuoteProbability.Builder quoteProbability() {
            return new QuoteProbability.Builder();
        }

        public static class Builder {
            private int percentageProbability = 0;
            private int distinctInstruments = 0;
            private int cancellationProbability = 0;

            public Builder withPercentageProbability(int percentageProbability) {
                this.percentageProbability = percentageProbability;
                return this;
            }

            public Builder withDistinctInstruments(int distinctInstruments) {
                this.distinctInstruments = distinctInstruments;
                return this;
            }

            public Builder withCancellationProbability(int noPriceProbability) {
                this.cancellationProbability = noPriceProbability;
                return this;
            }

            public QuoteProbability build() {
                return new QuoteProbability(percentageProbability, distinctInstruments, cancellationProbability);
            }
        }
    }
}
