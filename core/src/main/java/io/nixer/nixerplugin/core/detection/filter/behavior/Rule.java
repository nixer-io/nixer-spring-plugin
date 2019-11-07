package io.nixer.nixerplugin.core.detection.filter.behavior;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import org.springframework.util.Assert;

/**
 * Defines conditional rule for applying behavior
 */
public class Rule {

    private final String name;

    private final Predicate<Facts> predicate;

    private final AtomicReference<Behavior> behavior = new AtomicReference<>();

    public String name() {
        return name;
    }

    public boolean condition(final Facts facts) {
        return predicate.test(facts);
    }

    public Behavior behavior() {
        return behavior.get();
    }

    void updateBehavior(final Behavior behavior) {
        Assert.notNull(behavior, "Behavior must not be null");
        this.behavior.set(behavior);
    }

    Rule(final String name, final Predicate<Facts> predicate, final Behavior behavior) {
        this.name = name;
        this.predicate = predicate;
        this.behavior.set(behavior);
    }
}
