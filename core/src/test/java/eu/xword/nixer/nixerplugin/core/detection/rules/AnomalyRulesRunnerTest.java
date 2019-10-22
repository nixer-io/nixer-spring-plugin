package eu.xword.nixer.nixerplugin.core.detection.rules;

import java.util.ArrayList;
import java.util.List;

import eu.xword.nixer.nixerplugin.core.events.AnomalyEvent;
import eu.xword.nixer.nixerplugin.core.events.ApplicationEventPublisherStub;
import eu.xword.nixer.nixerplugin.core.events.IpFailedLoginOverThresholdEvent;
import eu.xword.nixer.nixerplugin.core.events.UserAgentFailedLoginOverThresholdEvent;
import eu.xword.nixer.nixerplugin.core.login.LoginContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnomalyRulesRunnerTest {

    private final ApplicationEventPublisherStub publisherStub = new ApplicationEventPublisherStub();

    @Test
    void shouldExecuteRulesAndPublishResultingEvents() {
        List<AnomalyRule> rules = new ArrayList<>();
        final UserAgentFailedLoginOverThresholdEvent userAgentEvent = new UserAgentFailedLoginOverThresholdEvent("user-agent");
        rules.add(fixedEventRule(userAgentEvent));

        final IpFailedLoginOverThresholdEvent ipEvent = new IpFailedLoginOverThresholdEvent("5.5.5.5");
        rules.add(fixedEventRule(ipEvent));

        rules.add(nopRule());

        final AnomalyRulesRunner rulesRunner = new AnomalyRulesRunner(publisherStub, rules);

        rulesRunner.onLogin(new LoginContext());

        assertThat(publisherStub.getEvents()).hasSize(2)
                .hasAtLeastOneElementOfType(UserAgentFailedLoginOverThresholdEvent.class)
                .hasAtLeastOneElementOfType(IpFailedLoginOverThresholdEvent.class);
    }

    private AnomalyRule nopRule() {
        return (loginContext, eventEmitter) -> {

        };
    }

    private AnomalyRule fixedEventRule(final AnomalyEvent event) {
        return (loginContext, eventEmitter) -> eventEmitter.accept(event);
    }

}