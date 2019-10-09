package eu.xword.nixer.nixerplugin.login.inmemory;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RollingCounterTest {

    private final ClockStub clockStub = new ClockStub();
    private RollingCounter counter;

    @BeforeEach
    void setup() {
        counter = new RollingCounter(Duration.ofMinutes(1));
        counter.setClock(clockStub);
    }

    @Test
    void should_increment_counter() {
        counter.add("a");

        assertEquals(1, counter.get("a"));
    }

    @Test
    void should_expire() {
        counter.add("a");
        clockStub.tick(Duration.ofMinutes(2));

        assertEquals(0, counter.get("a"));
        assertEquals(0, counter.keysSize());
    }

    @Test
    void should_remove_counter() {
        counter.add("a");
        counter.remove("a");

        assertEquals(0, counter.get("a"));
        assertEquals(0, counter.keysSize());
    }

    @Test
    void should_roll_over() {

        for (int i = 0; i < 10000; i++) {
            counter.add("a");
            clockStub.tick(Duration.ofMillis(1));
        }

        assertEquals(10000, counter.get("a"));
    }
}