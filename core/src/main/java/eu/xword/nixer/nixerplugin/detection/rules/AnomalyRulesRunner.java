package eu.xword.nixer.nixerplugin.detection.rules;

import java.util.Collections;
import java.util.List;

import eu.xword.nixer.nixerplugin.login.LoginContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

/**
 * Executes login anomaly detection rules
 */
public class AnomalyRulesRunner {

    private final List<AnomalyRule> anomalyRules;

    private final ApplicationEventPublisher eventPublisher;

    public AnomalyRulesRunner(final ApplicationEventPublisher eventPublisher, final List<AnomalyRule> rules) {
        Assert.notNull(eventPublisher, "ApplicationEventPublisher must not be null");
        this.eventPublisher = eventPublisher;

        Assert.notNull(rules, "rules must not be null");
        this.anomalyRules = Collections.unmodifiableList(rules);
    }

    public void onLogin(final LoginContext context) {
        for (AnomalyRule anomalyRule : anomalyRules) {
            anomalyRule.execute(context, eventPublisher::publishEvent);
        }
    }
}
