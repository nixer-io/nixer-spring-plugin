package eu.xword.nixer.nixerplugin.core.detection.filter.behavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.util.Assert;

/**
 * Builder allowing to define rule for behaviors.
 */
public class BehaviorProviderBuilder {

    private final List<Rule> rules = new ArrayList<>();
    private final BehaviorRegistry behaviorRegistry;

    private BehaviorProviderBuilder(final BehaviorRegistry behaviorRegistry) {
        Assert.notNull(behaviorRegistry, "BehaviorRegistry must not be null");
        this.behaviorRegistry = behaviorRegistry;
    }

    public static BehaviorProviderBuilder builder(final BehaviorRegistry behaviorRegistry) {
        return new BehaviorProviderBuilder(behaviorRegistry);
    }

    public RuleBuilder rule(final String name) {
        return new RuleBuilder(name);
    }

    public BehaviorProvider build() {
        validateRuleNameUniqueness();

        return new BehaviorProvider(rules);
    }

    private void validateRuleNameUniqueness() {
        final Set<String> uniqueNames = new HashSet<>();
        for (Rule rule : rules) {
            if (!uniqueNames.add(rule.name())) {
                throw new IllegalArgumentException("Rule with name" + rule.name() + " already registered");
            }
        }
    }

    public class RuleBuilder {

        private String name;

        private Predicate<Facts> predicate = (it) -> false;

        private String behaviorName;

        private RuleBuilder(final String name) {
            Assert.notNull(name, "Name must not be null");
            this.name = name;
        }

        public RuleBuilder when(final Predicate<Facts> predicate) {
            Assert.notNull(predicate, "Predicate must not be null");
            this.predicate = predicate;
            return this;
        }

        // todo find better name for act
        public RuleBuilder then(final Behaviors behavior) {
            Assert.notNull(behavior, "Behavior must not be null");

            return then(behavior.name());
        }

        public RuleBuilder then(final String behaviorName) {
            Assert.notNull(behaviorName, "BehaviorName must not be null");

            this.behaviorName = behaviorName;
            return this;
        }

        private Behavior lookupBehavior() {
            final Behavior behavior = behaviorRegistry.findByName(behaviorName);
            Assert.isTrue(behavior != null, () -> "Unknown behavior " + behaviorName);
            return behavior;
        }

        private Rule createRule() {
            Assert.notNull(name, "Name must not be null");
            Assert.notNull(predicate, "Predicate must not be null");
            Assert.notNull(behaviorName, "Behavior must not be null");

            final Behavior behavior = lookupBehavior();

            return new Rule(name, predicate, behavior);
        }

        public BehaviorProviderBuilder buildRule() {
            final Rule rule = createRule();
            rules.add(rule);
            return BehaviorProviderBuilder.this;
        }
    }
}
