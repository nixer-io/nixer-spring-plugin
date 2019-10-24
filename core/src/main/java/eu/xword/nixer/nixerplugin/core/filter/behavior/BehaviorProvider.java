package eu.xword.nixer.nixerplugin.core.filter.behavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;

/**
 * Allows to retrieve behaviors applicable for given {@link Facts}.
 */
public class BehaviorProvider {

    private final List<Rule> rules;

    public BehaviorProvider(final List<Rule> rules) {
        Assert.notNull(rules, "Rules must not be null");
        this.rules = Collections.unmodifiableList(rules);
    }

    public List<Behavior> get(final Facts facts) {
        Assert.notNull(facts, "Facts must not be null");

        List<Behavior> result = new ArrayList<>();
        for (Rule r : rules) {
            if (r.condition(facts)) {
                result.add(r.behavior());
            }
        }
        return result;
    }

    List<Rule> getRules() {
        return rules;
    }

    Rule getRule(final String ruleName) {
        Assert.notNull(ruleName, "RuleName must not be null");

        return rules.stream()
                .filter(it -> it.name().equals(ruleName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown rule " + ruleName));
    }



}
