package eu.xword.nixer.nixerplugin.detection.rules;

import java.util.ArrayList;
import java.util.List;

import eu.xword.nixer.nixerplugin.events.AnomalyEvent;
import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

/**
 * Executes login anomaly detection rules
 */
public class AnomalyRulesRunner implements EventEmitter {

    private final List<AnomalyRule> anomalyRules = new ArrayList<>();

    private final ApplicationEventPublisher eventPublisher;

    public AnomalyRulesRunner(final ApplicationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "ApplicationEventPublisher must not be null");
        this.eventPublisher = eventPublisher;
    }

    public void onLogin(final LoginResult result, final LoginContext context) {
        for (AnomalyRule anomalyRule : anomalyRules) {
            anomalyRule.execute(context, this);
        }
    }

    @Override
    public void accept(final AnomalyEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void addRule(AnomalyRule anomalyRule) {
        Assert.notNull(anomalyRule, "Rule must not be null");
        this.anomalyRules.add(anomalyRule);
    }
}
