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
        public final int permilProbability;

        public AckProbability(int permilProbability) {
            this.permilProbability = permilProbability;
        }
    }

    public static class QuoteProbability {
        public final int distinctInstruments;
        public final int perMillProbability;
        public final int cancellationPerMillProbability;

        private QuoteProbability(int perMillProbability, int distinctInstruments, int cancellationPerMillProbability) {
            this.distinctInstruments = distinctInstruments;
            this.perMillProbability = perMillProbability;
            this.cancellationPerMillProbability = cancellationPerMillProbability;
        }

        public static QuoteProbability.Builder quoteProbability() {
            return new QuoteProbability.Builder();
        }

        public static class Builder {
            private int perMillProbability = 0;
            private int distinctInstruments = 0;
            private int cancellationPerMillProbability = 0;

            public Builder withPercentageProbability(int perMillProbability) {
                this.perMillProbability = perMillProbability;
                return this;
            }

            public Builder withDistinctInstruments(int distinctInstruments) {
                this.distinctInstruments = distinctInstruments;
                return this;
            }

            public Builder withCancellationProbability(int noPriceProbability) {
                this.cancellationPerMillProbability = noPriceProbability;
                return this;
            }

            public QuoteProbability build() {
                return new QuoteProbability(perMillProbability, distinctInstruments, cancellationPerMillProbability);
            }
        }
    }
}
