package eu.xword.nixer.nixerplugin.filter.behavior;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BehaviorProviderTest {

    BehaviorProvider behaviorProvider;

    @BeforeEach
    public void init() {
        final BehaviorRegistry behaviorRegistry = new BehaviorRegistry();
        behaviorRegistry.init();
        behaviorProvider = new BehaviorProvider(behaviorRegistry);
    }

    @Test
    public void should_return_no_behaviors() {
        final Facts facts = new Facts(ImmutableMap.of());

        behaviorProvider.addRule("rule", it -> false, "captcha");

        final List<Behavior> behaviors = behaviorProvider.get(facts);

        assertThat(behaviors).isEmpty();
    }

    @Test
    public void should_return_multiple_matching_behaviors() {
        final Facts facts = new Facts(ImmutableMap.of());

        behaviorProvider.addRule("rule1", it -> true, "log");
        behaviorProvider.addRule("rule2", it -> false, "captcha");
        behaviorProvider.addRule("rule3", it -> true, "log");
        final List<Behavior> behaviors = behaviorProvider.get(facts);

        assertThat(behaviors).hasSize(2);
        assertThat(behaviors.get(0).name()).isEqualTo("log");
        assertThat(behaviors.get(1).name()).isEqualTo("log");
    }
}