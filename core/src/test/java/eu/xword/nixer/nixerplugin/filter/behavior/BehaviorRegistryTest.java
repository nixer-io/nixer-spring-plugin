package eu.xword.nixer.nixerplugin.filter.behavior;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class BehaviorRegistryTest {

    BehaviorRegistry registry;

    @BeforeEach
    public void setup() {
        registry = new BehaviorRegistry();
    }

    @Test
    public void should_find_by_name() {
        final LogBehavior logBehavior = new LogBehavior();
        registry.register(logBehavior);

        final Behavior found = registry.findByName("log");
        assertSame(found, logBehavior);
    }

    @Test
    public void should_throw_on_duplicate_name() {
        final LogBehavior logBehavior = new LogBehavior();
        registry.register(logBehavior);

        Assertions.assertThrows(IllegalArgumentException.class, () -> registry.register(logBehavior));
    }
}