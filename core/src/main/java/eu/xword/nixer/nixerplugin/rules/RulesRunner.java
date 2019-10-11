package eu.xword.nixer.nixerplugin.rules;

import java.util.ArrayList;
import java.util.List;

import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginResult;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

/**
 * Executes rules
 */
public class RulesRunner implements EventEmitter {

    private final List<Rule> rules = new ArrayList<>();

    private final ApplicationEventPublisher eventPublisher;

    public RulesRunner(final ApplicationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "ApplicationEventPublisher must not be null");
        this.eventPublisher = eventPublisher;
    }

    public void onLogin(final LoginResult result, final LoginContext context) {
        //todo should we skip execution for success login ?
        for (Rule rule : rules) {
            rule.execute(context, this);
        }
    }

    @Override
    public void emit(final ApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void addRule(Rule rule) {
        Assert.notNull(rule, "Rule must not be null");
        this.rules.add(rule);
    }
}
