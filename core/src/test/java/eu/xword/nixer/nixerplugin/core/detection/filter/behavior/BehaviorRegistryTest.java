package eu.xword.nixer.nixerplugin.core.detection.filter.behavior;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class BehaviorRegistryTest {

    private BehaviorRegistry registry;

    @BeforeEach
    void setup() {
        registry = new BehaviorRegistry();
    }

    @Test
    void should_find_by_name() {
        final LogBehavior logBehavior = new LogBehavior();
        registry.register(logBehavior);

        final Behavior found = registry.findByName(Behaviors.LOG.name());
        assertSame(found, logBehavior);
    }

    @Test
    void should_throw_on_duplicate_name() {
        final LogBehavior logBehavior = new LogBehavior();
        registry.register(logBehavior);

        Assertions.assertThrows(IllegalArgumentException.class, () -> registry.register(logBehavior));
    }
}