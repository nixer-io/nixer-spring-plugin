package io.nixer.nixerplugin.core.detection.filter.behavior;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BehaviorProviderTest {

    private static final Behavior PASSTHROUGH = new PassthroughBehavior();
    private static final Behavior LOG = new LogBehavior();

    private BehaviorProvider behaviorProvider;
    private Facts facts = new Facts(ImmutableMap.of());

    @BeforeEach
    void init() {
        final BehaviorRegistry behaviorRegistry = new BehaviorRegistry();
        behaviorRegistry.register(new LogBehavior());
        behaviorRegistry.register(new PassthroughBehavior());
    }

    @Test
    void should_return_no_behaviors() {
        givenRules(
                new Rule("rule", it -> false, PASSTHROUGH)
        );

        final List<Behavior> behaviors = applyFacts();

        assertThat(behaviors).isEmpty();
    }

    @Test
    void should_return_multiple_matching_behaviors() {
        givenRules(
                new Rule("rule1", it -> true, LOG),
                new Rule("rule2", it -> false, PASSTHROUGH),
                new Rule("rule3", it -> true, LOG)
        );

        final List<Behavior> behaviors = applyFacts();

        assertThat(behaviors).containsExactly(LOG, LOG);
    }

    private List<Behavior> applyFacts() {
        return behaviorProvider.get(facts);
    }

    private void givenRules(final Rule... rules) {
        behaviorProvider = new BehaviorProvider(ImmutableList.copyOf(rules));
    }

}
