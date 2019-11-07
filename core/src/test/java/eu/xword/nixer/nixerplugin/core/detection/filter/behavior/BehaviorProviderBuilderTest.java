package eu.xword.nixer.nixerplugin.core.detection.filter.behavior;

import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static eu.xword.nixer.nixerplugin.core.detection.filter.behavior.BehaviorProviderBuilder.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BehaviorProviderBuilderTest {

    private static final String RULE_NAME = "test-rule";
    private static final String OTHER_RULE_NAME = "test-rule2";
    private static final Predicate<Facts> RULE_PREDICATE = (it) -> true;

    private BehaviorRegistry behaviorRegistry = new BehaviorRegistry();
    private LogBehavior logBehavior = new LogBehavior();

    @BeforeEach
    void setup() {
        this.behaviorRegistry.register(logBehavior);
    }

    @Test
    void shouldBuildProviderWithRule() {
        final BehaviorProvider provider = builder(behaviorRegistry)
                .rule(RULE_NAME)
                .when(RULE_PREDICATE)
                .then(Behaviors.LOG).buildRule()
                .build();

        final Rule rule = provider.getRule(RULE_NAME);

        assertNotNull(rule);
        assertEquals(logBehavior, rule.behavior());
        assertEquals(RULE_NAME, rule.name());
    }

    @Test
    void shouldBuildProviderWithMultipleRules() {
        final BehaviorProvider provider = builder(behaviorRegistry)
                .rule(RULE_NAME)
                .when(RULE_PREDICATE)
                .then(Behaviors.LOG).buildRule()
                .rule(OTHER_RULE_NAME)
                .when(RULE_PREDICATE)
                .then(Behaviors.LOG).buildRule()
                .build();


        assertNotNull(provider.getRule(RULE_NAME));
        assertNotNull(provider.getRule(OTHER_RULE_NAME));
    }


    @Test
    void shouldFailOnDuplicateRuleName() {
        final BehaviorProviderBuilder providerBuilder = builder(behaviorRegistry)
                .rule(RULE_NAME)
                .when(RULE_PREDICATE)
                .then(Behaviors.LOG).buildRule()
                .rule(RULE_NAME)
                .when(RULE_PREDICATE)
                .then(Behaviors.LOG).buildRule();

        assertThrows(IllegalArgumentException.class, providerBuilder::build);
    }

    @Test
    void shouldFailOnUnknownBehavior() {
        assertThrows(IllegalArgumentException.class, () -> {
            builder(behaviorRegistry)
                    .rule(RULE_NAME)
                    .when(RULE_PREDICATE)
                    .then("unknownBehavior").buildRule();
        });
    }
}