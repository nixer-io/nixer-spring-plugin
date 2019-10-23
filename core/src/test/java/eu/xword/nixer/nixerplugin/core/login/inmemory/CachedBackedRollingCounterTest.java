package eu.xword.nixer.nixerplugin.core.login.inmemory;

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
        counter.increment("a");

        assertEquals(1, counter.count("a"));
    }

    @Test
    void should_increment_counters_independently() {
        counter.increment("a");
        counter.increment("b");
        counter.increment("b");

        assertEquals(1, counter.count("a"));
        assertEquals(2, counter.count("b"));
    }

    @Test
    void should_expire() {
        counter.increment("a");
        clockStub.tick(Duration.ofMinutes(2));

        assertEquals(0, counter.count("a"));
        assertEquals(0, counter.keysSize());
    }

    @Test
    void should_remove_counter() {
        counter.increment("a");
        counter.remove("a");

        assertEquals(0, counter.count("a"));
        assertEquals(0, counter.keysSize());
    }

    @Test
    void should_count_only_within_window() {
        for (int i = 0; i < 200; i++) {
            counter.increment("a");
            clockStub.tick(Duration.ofSeconds(1));
        }

        assertEquals(100, counter.count("a"));
    }
}