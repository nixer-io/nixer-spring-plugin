package eu.xword.nixer.nixerplugin.filter.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.util.Assert;

/**
 * Builder allowing to define rule for behaviors.
 */
public class BehaviourProviderBuilder {

    private final List<RuleDefinition> ruleDefinitions = new ArrayList<>();

    private BehaviourProviderBuilder() {
    }

    public static BehaviourProviderBuilder builder() {
        return new BehaviourProviderBuilder();
    }

    public RuleBuilder rule(final String name) {
        return new RuleBuilder(name);
    }

    private BehaviourProviderBuilder addRuleDefinition(final RuleDefinition ruleDefinition) {
        Assert.notNull(ruleDefinition, "RuleDefinition must not be null");

        ruleDefinitions.add(ruleDefinition);
        return this;
    }

    public BehaviorProvider build(BehaviorRegistry behaviorRegistry) {
        final BehaviorProvider behaviorProvider = new BehaviorProvider(behaviorRegistry);

        final long uniqueRuleNamesCount = ruleDefinitions.stream().map(ruleDefinition -> ruleDefinition.name).distinct().count();
        Assert.isTrue(ruleDefinitions.size() == uniqueRuleNamesCount, "Found duplicate rule names");

        ruleDefinitions.forEach(ruleDefinition -> {
            behaviorProvider.addRule(ruleDefinition.name, ruleDefinition.predicate, ruleDefinition.behavior);
        });

        return behaviorProvider;
    }

    private static class RuleDefinition {
        private String name;

        private Predicate<Facts> predicate;

        private String behavior;

        private RuleDefinition(RuleBuilder builder) {
            Assert.notNull(builder.name, "Name must not be null");
            this.name = builder.name;

            Assert.notNull(builder.predicate, "Predicate must not be null");
            this.predicate = builder.predicate;

            Assert.notNull(builder.behavior, "Behavior must not be null");
            this.behavior = builder.behavior;
        }
    }

    public class RuleBuilder {

        private String name;

        private Predicate<Facts> predicate = (it) -> false;

        private String behavior;

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
        public RuleBuilder act(final Behaviors behavior) {
            Assert.notNull(behavior, "Behavior must not be null");

            return act(behavior.name());
        }

        public RuleBuilder act(final String behavior) {
            Assert.notNull(behavior, "Behavior must not be null");
            this.behavior = behavior;
            return this;
        }

        public BehaviourProviderBuilder build() {
            return BehaviourProviderBuilder.this.addRuleDefinition(new RuleDefinition(this));
        }
    }
}
