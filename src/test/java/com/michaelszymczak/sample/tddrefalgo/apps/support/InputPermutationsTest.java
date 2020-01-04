package com.michaelszymczak.sample.tddrefalgo.apps.support;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class InputPermutationsTest {

    @Test
    void shouldGenerateNoPermutationsForNoChoices() {
        InputPermutations<?> permutations = new InputPermutations<>(emptyList());
        assertThat(permutations.generate(1)).isEmpty();
    }

    @Test
    void shouldGenerateNoPermutationsForNoSlots() {
        InputPermutations<String> permutations = new InputPermutations<>("A", "B", "C");
        assertThat(permutations.generate(0)).isEmpty();
    }

    @Test
    void shouldGenerateEachInput() {
        InputPermutations<String> permutations = new InputPermutations<>("A", "B", "C");
        assertThat(permutations.generate(1)).isEqualTo(asList(
                singletonList("A"),
                singletonList("B"),
                singletonList("C")
        ));
    }

    @Test
    void shouldFillAllTheSlots() {
        InputPermutations<String> permutations = new InputPermutations<>("A", "B");
        assertThat(permutations.generate(2)).isEqualTo(asList(
                asList("A", "A"),
                asList("A", "B"),
                asList("B", "A"),
                asList("B", "B")
        ));
    }

    @Test
    void shouldSupportArbitraryTypes() {
        InputPermutations<Integer> permutations = new InputPermutations<>(50, 60, 70);
        assertThat(permutations.generate(4)).isEqualTo(asList(
                asList(50, 50, 50, 50),
                asList(50, 50, 50, 60),
                asList(50, 50, 50, 70),
                asList(50, 50, 60, 50),
                asList(50, 50, 60, 60),
                asList(50, 50, 60, 70),
                asList(50, 50, 70, 50),
                asList(50, 50, 70, 60),
                asList(50, 50, 70, 70),
                asList(50, 60, 50, 50),
                asList(50, 60, 50, 60),
                asList(50, 60, 50, 70),
                asList(50, 60, 60, 50),
                asList(50, 60, 60, 60),
                asList(50, 60, 60, 70),
                asList(50, 60, 70, 50),
                asList(50, 60, 70, 60),
                asList(50, 60, 70, 70),
                asList(50, 70, 50, 50),
                asList(50, 70, 50, 60),
                asList(50, 70, 50, 70),
                asList(50, 70, 60, 50),
                asList(50, 70, 60, 60),
                asList(50, 70, 60, 70),
                asList(50, 70, 70, 50),
                asList(50, 70, 70, 60),
                asList(50, 70, 70, 70),
                asList(60, 50, 50, 50),
                asList(60, 50, 50, 60),
                asList(60, 50, 50, 70),
                asList(60, 50, 60, 50),
                asList(60, 50, 60, 60),
                asList(60, 50, 60, 70),
                asList(60, 50, 70, 50),
                asList(60, 50, 70, 60),
                asList(60, 50, 70, 70),
                asList(60, 60, 50, 50),
                asList(60, 60, 50, 60),
                asList(60, 60, 50, 70),
                asList(60, 60, 60, 50),
                asList(60, 60, 60, 60),
                asList(60, 60, 60, 70),
                asList(60, 60, 70, 50),
                asList(60, 60, 70, 60),
                asList(60, 60, 70, 70),
                asList(60, 70, 50, 50),
                asList(60, 70, 50, 60),
                asList(60, 70, 50, 70),
                asList(60, 70, 60, 50),
                asList(60, 70, 60, 60),
                asList(60, 70, 60, 70),
                asList(60, 70, 70, 50),
                asList(60, 70, 70, 60),
                asList(60, 70, 70, 70),
                asList(70, 50, 50, 50),
                asList(70, 50, 50, 60),
                asList(70, 50, 50, 70),
                asList(70, 50, 60, 50),
                asList(70, 50, 60, 60),
                asList(70, 50, 60, 70),
                asList(70, 50, 70, 50),
                asList(70, 50, 70, 60),
                asList(70, 50, 70, 70),
                asList(70, 60, 50, 50),
                asList(70, 60, 50, 60),
                asList(70, 60, 50, 70),
                asList(70, 60, 60, 50),
                asList(70, 60, 60, 60),
                asList(70, 60, 60, 70),
                asList(70, 60, 70, 50),
                asList(70, 60, 70, 60),
                asList(70, 60, 70, 70),
                asList(70, 70, 50, 50),
                asList(70, 70, 50, 60),
                asList(70, 70, 50, 70),
                asList(70, 70, 60, 50),
                asList(70, 70, 60, 60),
                asList(70, 70, 60, 70),
                asList(70, 70, 70, 50),
                asList(70, 70, 70, 60),
                asList(70, 70, 70, 70)
        ));
    }
}