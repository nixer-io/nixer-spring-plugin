package eu.xword.nixer.nixerplugin.core.filter.behavior;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BehaviorProviderTest {

    private static final String ERROR = Behaviors.PASSTHROUGH.name();
    private static final String LOG = Behaviors.LOG.name();
    private BehaviorProvider behaviorProvider;

    @BeforeEach
    void init() {
        final BehaviorRegistry behaviorRegistry = new BehaviorRegistry();
        behaviorRegistry.afterPropertiesSet();
        behaviorProvider = new BehaviorProvider(behaviorRegistry);
    }

    @Test
    void should_return_no_behaviors() {
        final Facts facts = new Facts(ImmutableMap.of());

        behaviorProvider.addRule("rule", it -> false, ERROR);

        final List<Behavior> behaviors = behaviorProvider.get(facts);

        assertThat(behaviors).isEmpty();
    }

    @Test
    void should_return_multiple_matching_behaviors() {
        final Facts facts = new Facts(ImmutableMap.of());

        behaviorProvider.addRule("rule1", it -> true, LOG);
        behaviorProvider.addRule("rule2", it -> false, ERROR);
        behaviorProvider.addRule("rule3", it -> true, LOG);
        final List<Behavior> behaviors = behaviorProvider.get(facts);

        assertThat(behaviors).hasSize(2);
        assertThat(behaviors.get(0).name()).isEqualTo(LOG);
        assertThat(behaviors.get(1).name()).isEqualTo(LOG);
    }
}
