package io.nixer.nixerplugin.core.detection.rules;

import java.util.Collections;
import java.util.List;

import io.nixer.nixerplugin.core.login.LoginContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

/**
 * Rules runner keeps collection of rules and executes them on demand
 */
public class RulesRunner {

    private final List<LoginRule> loginRules;

    private final ApplicationEventPublisher eventPublisher;

    public RulesRunner(final ApplicationEventPublisher eventPublisher, final List<LoginRule> rules) {
        Assert.notNull(eventPublisher, "ApplicationEventPublisher must not be null");
        this.eventPublisher = eventPublisher;

        Assert.notNull(rules, "rules must not be null");
        this.loginRules = Collections.unmodifiableList(rules);
    }

    public void onLogin(final LoginContext context) {
        for (LoginRule loginRule : loginRules) {
            loginRule.execute(context, eventPublisher::publishEvent);
        }
    }
}
