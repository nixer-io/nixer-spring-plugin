package io.nixer.nixerplugin.core.detection.rules;

import java.util.ArrayList;
import java.util.List;

import io.nixer.nixerplugin.core.detection.events.AnomalyEvent;
import io.nixer.nixerplugin.core.detection.events.ApplicationEventPublisherStub;
import io.nixer.nixerplugin.core.detection.events.IpFailedLoginOverThresholdEvent;
import io.nixer.nixerplugin.core.detection.events.UserAgentFailedLoginOverThresholdEvent;
import io.nixer.nixerplugin.core.login.LoginContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class RulesRunnerTest {

    private final ApplicationEventPublisherStub publisherStub = new ApplicationEventPublisherStub();

    @Test
    void shouldExecuteRulesAndPublishResultingEvents() {
        List<LoginRule> rules = new ArrayList<>();
        final UserAgentFailedLoginOverThresholdEvent userAgentEvent = new UserAgentFailedLoginOverThresholdEvent("user-agent");
        rules.add(fixedEventRule(userAgentEvent));

        final IpFailedLoginOverThresholdEvent ipEvent = new IpFailedLoginOverThresholdEvent("5.5.5.5");
        rules.add(fixedEventRule(ipEvent));

        rules.add(nopRule());

        final RulesRunner rulesRunner = new RulesRunner(publisherStub, rules);

        rulesRunner.onLogin(Mockito.mock(LoginContext.class));

        assertThat(publisherStub.getEvents()).hasSize(2)
                .hasAtLeastOneElementOfType(UserAgentFailedLoginOverThresholdEvent.class)
                .hasAtLeastOneElementOfType(IpFailedLoginOverThresholdEvent.class);
    }

    private LoginRule nopRule() {
        return (loginContext, eventEmitter) -> {

        };
    }

    private LoginRule fixedEventRule(final AnomalyEvent event) {
        return (loginContext, eventEmitter) -> eventEmitter.accept(event);
    }

}
