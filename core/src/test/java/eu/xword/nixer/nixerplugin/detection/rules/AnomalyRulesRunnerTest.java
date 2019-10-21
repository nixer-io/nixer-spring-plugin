package eu.xword.nixer.nixerplugin.detection.rules;

import eu.xword.nixer.nixerplugin.events.AnomalyEvent;
import eu.xword.nixer.nixerplugin.events.ApplicationEventPublisherStub;
import eu.xword.nixer.nixerplugin.events.IpFailedLoginOverThresholdEvent;
import eu.xword.nixer.nixerplugin.events.UserAgentFailedLoginOverThresholdEvent;
import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginFailureType;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class AnomalyRulesRunnerTest {

    private final ApplicationEventPublisherStub publisherStub = new ApplicationEventPublisherStub();
    private final AnomalyRulesRunner rulesRunner = new AnomalyRulesRunner(publisherStub);

    @Test
    void shouldExecuteRulesAndPublishResultingEvents() {
        final UserAgentFailedLoginOverThresholdEvent userAgentEvent = new UserAgentFailedLoginOverThresholdEvent("user-agent");
        rulesRunner.addRule(new FixedEventEmittingRule(userAgentEvent));

        final IpFailedLoginOverThresholdEvent ipEvent = new IpFailedLoginOverThresholdEvent("5.5.5.5");
        rulesRunner.addRule(new FixedEventEmittingRule(ipEvent));

        rulesRunner.addRule(new NOPRule());

        rulesRunner.onLogin(LoginResult.failure(LoginFailureType.BAD_PASSWORD), new LoginContext());

        assertThat(publisherStub.getEvents()).hasSize(2)
                .hasAtLeastOneElementOfType(UserAgentFailedLoginOverThresholdEvent.class)
                .hasAtLeastOneElementOfType(IpFailedLoginOverThresholdEvent.class);
    }

    private static class NOPRule implements AnomalyRule {

        @Override
        public void execute(final LoginContext loginContext, final EventEmitter eventEmitter) {

        }
    }

    private static class FixedEventEmittingRule implements AnomalyRule {

        private final AnomalyEvent event;

        private FixedEventEmittingRule(final AnomalyEvent event) {
            this.event = event;
        }

        @Override
        public void execute(final LoginContext loginContext, final EventEmitter eventEmitter) {
            eventEmitter.accept(event);
        }
    }
}