package eu.xword.nixer.nixerplugin.login.inmemory;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CachedBackedRollingCounterTest {

    private final ClockStub clockStub = new ClockStub();
    private CachedBackedRollingCounter counter;

    @BeforeEach
    void setup() {
        counter = new CachedBackedRollingCounter(Duration.ofSeconds(100));
        counter.setClock(clockStub);
    }

    @Test
    void should_increment_counter() {
        counter.add("a", 1);

        assertEquals(1, counter.get("a"));
    }

    @Test
    void should_increment_counters_independently() {
        counter.add("a", 1);
        counter.add("b", 1);
        counter.add("b", 1);

        assertEquals(1, counter.get("a"));
        assertEquals(2, counter.get("b"));
    }

    @Test
    void should_expire() {
        counter.add("a", 1);
        clockStub.tick(Duration.ofMinutes(2));

        assertEquals(0, counter.get("a"));
        assertEquals(0, counter.keysSize());
    }

    @Test
    void should_remove_counter() {
        counter.add("a", 1);
        counter.remove("a");

        assertEquals(0, counter.get("a"));
        assertEquals(0, counter.keysSize());
    }

    @Test
    void should_count_only_within_window() {
        for (int i = 0; i < 200; i++) {
            counter.add("a", 1);
            clockStub.tick(Duration.ofSeconds(1));
        }

        assertEquals(100, counter.get("a"));
    }
}