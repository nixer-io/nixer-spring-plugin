package eu.xword.nixer.nixerplugin.filter.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.util.Assert;

public class BehaviourProviderBuilder {

    private final List<RuleDefinition> ruleDefinitions = new ArrayList<>();

    private BehaviourProviderBuilder() {
    }

    public static BehaviourProviderBuilder builder() {
        return new BehaviourProviderBuilder();
    }

    public BehaviourProviderBuilder addRule(final String name, final Predicate<Facts> predicate, final String behavior) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(predicate, "Predicate must not be null");
        Assert.notNull(behavior, "behavior must not be null");

        ruleDefinitions.add(new RuleDefinition(name, predicate, behavior));
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

    public static class RuleDefinition {
        private String name;

        Predicate<Facts> predicate;

        private String behavior;

        public RuleDefinition(final String name, final Predicate<Facts> predicate, final String behavior) {
            this.name = name;
            this.predicate = predicate;
            this.behavior = behavior;
        }
    }
}
