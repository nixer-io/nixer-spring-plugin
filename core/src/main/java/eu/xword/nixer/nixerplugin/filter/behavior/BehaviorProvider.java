package eu.xword.nixer.nixerplugin.filter.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;
import org.springframework.util.Assert;

public class BehaviorProvider {

    private final BehaviorRegistry behaviorRegistry;

    private final ConcurrentHashMap<String, String> behaviors = new ConcurrentHashMap<>();

    private final List<Rule> rules = new ArrayList<>();

    public BehaviorProvider(final BehaviorRegistry behaviorRegistry) {
        Assert.notNull(behaviorRegistry, "BehaviorRegistry must not be null");
        this.behaviorRegistry = behaviorRegistry;
    }

    public void addRule(String name, Predicate<Facts> predicate, String behaviorName) {
        Optional.ofNullable(behaviorRegistry.findByName(behaviorName))
                .orElseThrow(() -> new IllegalArgumentException("Unknown behavior " + behaviorName));

        behaviors.put(name, behaviorName);
        rules.add(new PredicateRule(name, predicate));
    }

    public List<Behavior> get(Facts facts) {
        Assert.notNull(facts, "Facts must not be null");

        List<Behavior> result = new ArrayList<>();
        for (Rule r : rules) {
            if (r.condition(facts)) {
                String behaviorName = behaviors.get(r.name());
                final Behavior behavior = behaviorRegistry.findByName(behaviorName);

                result.add(behavior);
            }
        }
        return result;
    }

    public Map<String, String> getRuleBehaviors() {
        return ImmutableMap.copyOf(behaviors);
    }


    public void setBehavior(String ruleName, String behaviorName) {
        Assert.notNull(ruleName, "RuleName must not be null");
        Assert.notNull(behaviorName, "BehaviorName must not be null");

        final Behavior behavior = behaviorRegistry.findByName(behaviorName);
        Assert.notNull(behavior, "Behavior must not be null");

        behaviors.put(ruleName, behaviorName);
    }
}
